package com.inkos.core.pipeline;

import com.inkos.core.agent.*;
import com.inkos.core.llm.LlmProvider;
import com.inkos.core.model.BookProfile;
import com.inkos.core.model.ChapterContent;
import com.inkos.core.state.StateManager;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PipelineRunner {

  private final LlmProvider llmProvider;
  private final String model;
  private final Path projectRoot;
  private final PipelineConfig config;
  private final StateManager stateManager;
  private final AgentRegistry agentRegistry;

  public PipelineRunner(
    LlmProvider llmProvider,
    String model,
    Path projectRoot,
    PipelineConfig config,
    StateManager stateManager
  ) {
    this.llmProvider = llmProvider;
    this.model = model;
    this.projectRoot = projectRoot;
    this.config = config;
    this.stateManager = stateManager;
    this.agentRegistry = new AgentRegistry();
  }

  public PipelineRunner(
    LlmProvider llmProvider,
    String model,
    Path projectRoot,
    PipelineConfig config,
    StateManager stateManager,
    AgentRegistry agentRegistry
  ) {
    this.llmProvider = llmProvider;
    this.model = model;
    this.projectRoot = projectRoot;
    this.config = config;
    this.stateManager = stateManager;
    this.agentRegistry = agentRegistry;
  }

  public AgentRegistry getAgentRegistry() {
    return agentRegistry;
  }

  private AgentContext buildContext(String bookId) {
    return AgentContext.builder()
      .llmProvider(llmProvider)
      .model(model)
      .projectRoot(projectRoot)
      .bookId(bookId)
      .build();
  }

  private Map<String, Object> buildExtra(Map<String, Object> overrides) {
    var map = new HashMap<String, Object>();
    map.putAll(overrides);
    return map;
  }

  // ── Book initialization ───────────────────────────────────────────────

  public CompletableFuture<Void> initBook(
    String bookId,
    String title,
    String language,
    String authorIntent
  ) {
    var ctx = buildContext(bookId);
    return stateManager.ensureControlDocuments(bookId, authorIntent)
      .thenCompose(v -> {
        var profile = new BookProfile(
          bookId, 0L, title, BookProfile.PLATFORM_TOMATO,
          "xuanhuan", BookProfile.STATUS_OUTLINING,
          200, 3000, language, null, 0, "", null,
          null, null, null
        );

        var architectInput = AgentInput.builder()
          .prompt(AgentPromptBuilder.architectUserPrompt(profile))
          .systemPrompt(AgentPromptBuilder.architectSystemPrompt(profile))
          .extra(buildExtra(Map.of("profile", profile)))
          .build();

        return agentRegistry.getAgent("architect")
          .execute(ctx, architectInput)
          .thenApply(result -> {
            if (!result.success()) {
              throw new RuntimeException("Architect failed: " + result.error());
            }
            return result;
          });
      })
      .thenCompose(result -> {
        var focusContent = "# 当前聚焦\n\n## 当前重点\n\n小说《" + title + "》正式开始创作。";
        return stateManager.updateCurrentFocus(bookId, focusContent)
          .thenApply(v -> result);
      })
      .thenApply(v -> null);
  }

  // ── Chapter planning ─────────────────────────────────────────────────

  public CompletableFuture<String> planChapter(String bookId, int chapterNumber) {
    var ctx = buildContext(bookId);
    return stateManager.loadControlDocuments(bookId)
      .thenCompose(docs -> stateManager.readChapter(bookId, chapterNumber - 1)
        .thenCompose(prevChapter -> {
          var profile = resolveProfile(bookId);

          var extra = buildExtra(Map.of(
            "profile", profile,
            "chapterNumber", chapterNumber,
            "storyState", prevChapter.orElse("（本书第一章，无前章）"),
            "currentFocus", docs.currentFocus(),
            "authorIntent", docs.authorIntent(),
            "volumeOutline", "",
            "characterContext", ""
          ));

          var input = AgentInput.builder()
            .prompt(extra.toString())
            .systemPrompt(AgentPromptBuilder.plannerSystemPrompt(profile))
            .extra(extra)
            .build();

          return agentRegistry.getAgent("planner")
            .execute(ctx, input)
            .thenApply(result -> {
              if (!result.success()) {
                throw new RuntimeException("Planner failed: " + result.error());
              }
              return result.content();
            });
        }));
  }

  // ── Chapter writing ──────────────────────────────────────────────────

  public CompletableFuture<WriteChapterOutput> writeDraft(WriteChapterInput input) {
    var ctx = buildContext(input.bookId());
    var profile = resolveProfile(input.bookId());

    var startTime = System.currentTimeMillis();

    return stateManager.loadControlDocuments(input.bookId())
      .thenCompose(docs -> stateManager.readChapter(input.bookId(), input.chapterNumber() - 1)
        .thenCompose(prevChapter -> {
          var extra = buildExtra(Map.of(
            "profile", profile,
            "chapterNumber", input.chapterNumber(),
            "chapterIntent", input.planJson(),
            "storyState", docs.currentFocus(),
            "storyBible", input.authorIntent(),
            "recentChapters", prevChapter.orElse(""),
            "pendingHooks", "",
            "characterMatrix", input.characterContext()
          ));

          var writerInput = AgentInput.builder()
            .prompt(input.planJson())
            .systemPrompt(AgentPromptBuilder.writerSystemPrompt(profile))
            .extra(extra)
            .build();

          return agentRegistry.getAgent("writer")
            .execute(ctx, writerInput)
            .thenCompose(writeResult -> {
              if (!writeResult.success()) {
                return CompletableFuture.completedFuture(
                  WriteChapterOutput.builder()
                    .chapterContent("")
                    .chapterTitle(input.chapterTitle())
                    .chapterNumber(input.chapterNumber())
                    .totalTokens(0)
                    .writeDurationMs(System.currentTimeMillis() - startTime)
                    .auditResult(new WriteChapterOutput.AuditResult(false, 1,
                      List.of("Writer failed: " + writeResult.error()), 0.0))
                    .revisionHistory(List.of())
                    .accepted(false)
                    .warnings(List.of("Writer agent failed"))
                    .build()
                );
              }

              var chapterContent = writeResult.content();
              var observerExtra = buildExtra(Map.of(
                "profile", profile,
                "chapterNumber", input.chapterNumber(),
                "chapterTitle", input.chapterTitle()
              ));

              var observerInput = AgentInput.builder()
                .prompt(chapterContent)
                .systemPrompt(AgentPromptBuilder.observerSystemPrompt(profile))
                .extra(observerExtra)
                .build();

              return agentRegistry.getAgent("observer")
                .execute(ctx, observerInput)
                .thenCompose(obsResult -> {
                  var reflectorExtra = buildExtra(Map.of(
                    "profile", profile,
                    "chapterNumber", input.chapterNumber(),
                    "currentState", docs.currentFocus(),
                    "ledger", "",
                    "pendingHooks", ""
                  ));

                  var reflectorInput = AgentInput.builder()
                    .prompt(obsResult.success() ? obsResult.content() : "(no observations)")
                    .systemPrompt(AgentPromptBuilder.reflectorSystemPrompt(profile))
                    .extra(reflectorExtra)
                    .build();

                  return agentRegistry.getAgent("reflector")
                    .execute(ctx, reflectorInput)
                    .thenApply(reflectResult -> {
                      var warnings = new ArrayList<String>();
                      if (!obsResult.success()) warnings.add("Observer warning: " + obsResult.error());
                      if (!reflectResult.success()) warnings.add("Reflector warning: " + reflectResult.error());

                      var auditResult = new WriteChapterOutput.AuditResult(true, 0,
                        List.of(), 1.0);

                      return WriteChapterOutput.builder()
                        .chapterContent(chapterContent)
                        .chapterTitle(input.chapterTitle())
                        .chapterNumber(input.chapterNumber())
                        .totalTokens(0)
                        .writeDurationMs(System.currentTimeMillis() - startTime)
                        .auditResult(auditResult)
                        .revisionHistory(List.of())
                        .accepted(true)
                        .warnings(warnings)
                        .build();
                    });
                });
            });
        }));
  }

  // ── Audit ────────────────────────────────────────────────────────────

  public CompletableFuture<WriteChapterOutput.AuditResult> auditDraft(
    String bookId, String chapterContent
  ) {
    var ctx = buildContext(bookId);
    var profile = resolveProfile(bookId);

    return stateManager.loadControlDocuments(bookId)
      .thenCompose(docs -> {
        var extra = buildExtra(Map.of(
          "profile", profile,
          "storyBible", docs.authorIntent(),
          "characterMatrix", "",
          "pendingHooks", ""
        ));

        var input = AgentInput.builder()
          .prompt(chapterContent)
          .systemPrompt(AgentPromptBuilder.auditorSystemPrompt(profile))
          .extra(extra)
          .build();

        return agentRegistry.getAgent("auditor")
          .execute(ctx, input)
          .thenApply(result -> {
            if (!result.success()) {
              return new WriteChapterOutput.AuditResult(false, 1,
                List.of("Audit failed: " + result.error()), 0.0);
            }

            var issues = parseAuditIssues(result.content());
            long criticalCount = issues.stream()
              .filter(i -> i.contains("critical"))
              .count();
            boolean passed = criticalCount == 0;

            return new WriteChapterOutput.AuditResult(
              passed, issues.size(), issues, passed ? 1.0 : 0.5
            );
          });
      });
  }

  // ── Revision ─────────────────────────────────────────────────────────

  public CompletableFuture<WriteChapterOutput> reviseDraft(
    String bookId,
    String chapterContent,
    WriteChapterOutput.AuditResult auditResult
  ) {
    var ctx = buildContext(bookId);
    int maxCycles = config.maxReviewCycles();
    var startTime = System.currentTimeMillis();
    int chapterNumber = 0;

    return reviseWithRetry(ctx, bookId, chapterContent, auditResult, maxCycles, startTime, chapterNumber);
  }

  private CompletableFuture<WriteChapterOutput> reviseWithRetry(
    AgentContext ctx, String bookId, String chapterContent,
    WriteChapterOutput.AuditResult auditResult, int remainingCycles,
    long startTime, int chapterNumber
  ) {
    var issues = new ArrayList<AgentResult.AuditIssue>();
    for (var issue : auditResult.issues()) {
      issues.add(new AgentResult.AuditIssue("warning", "audit", issue, ""));
    }

    var extra = buildExtra(Map.of(
      "chapterNumber", chapterNumber,
      "reviseMode", ReviserAgent.DEFAULT_REVISE_MODE,
      "issues", issues
    ));

    var input = AgentInput.builder()
      .prompt(chapterContent)
      .systemPrompt(AgentPromptBuilder.reviserSystemPrompt())
      .extra(extra)
      .build();

    return agentRegistry.getAgent("reviser")
      .execute(ctx, input)
      .thenCompose(result -> {
        if (!result.success()) {
          return CompletableFuture.completedFuture(
            WriteChapterOutput.builder()
              .chapterContent(chapterContent)
              .chapterTitle("")
              .chapterNumber(chapterNumber)
              .totalTokens(0)
              .writeDurationMs(System.currentTimeMillis() - startTime)
              .auditResult(auditResult)
              .revisionHistory(List.of())
              .accepted(false)
              .warnings(List.of("Revision failed"))
              .build()
          );
        }

        if (remainingCycles <= 0) {
          return CompletableFuture.completedFuture(
            WriteChapterOutput.builder()
              .chapterContent(result.content())
              .chapterTitle("")
              .chapterNumber(chapterNumber)
              .totalTokens(0)
              .writeDurationMs(System.currentTimeMillis() - startTime)
              .auditResult(auditResult)
              .revisionHistory(List.of())
              .accepted(true)
              .warnings(List.of("Max revision cycles reached"))
              .build()
          );
        }

        return auditDraft(bookId, result.content())
          .thenCompose(newAudit -> {
            if (newAudit.passed()) {
              return CompletableFuture.completedFuture(
                WriteChapterOutput.builder()
                  .chapterContent(result.content())
                  .chapterTitle("")
                  .chapterNumber(chapterNumber)
                  .totalTokens(0)
                  .writeDurationMs(System.currentTimeMillis() - startTime)
                  .auditResult(newAudit)
                  .revisionHistory(List.of())
                  .accepted(true)
                  .warnings(List.of())
                  .build()
              );
            }
            return reviseWithRetry(ctx, bookId, result.content(),
              newAudit, remainingCycles - 1, startTime, chapterNumber);
          });
      });
  }

  // ── Compose (plan → write → review) ──────────────────────────────────

  public CompletableFuture<WriteChapterOutput> composeChapter(
    String bookId,
    int chapterNumber,
    String planJson
  ) {
    var input = WriteChapterInput.builder()
      .bookId(bookId)
      .chapterNumber(chapterNumber)
      .chapterTitle("")
      .planJson(planJson)
      .authorIntent("")
      .currentFocus("")
      .styleGuide("")
      .characterContext(Map.of())
      .build();

    return writeDraft(input)
      .thenCompose(draft -> {
        if (!draft.accepted()) {
          return CompletableFuture.completedFuture(draft);
        }

        return auditDraft(bookId, draft.chapterContent())
          .thenCompose(audit -> {
            if (audit.passed()) {
              return CompletableFuture.completedFuture(
                new WriteChapterOutput(
                  draft.chapterContent(), draft.chapterTitle(),
                  draft.chapterNumber(), draft.totalTokens(),
                  draft.writeDurationMs(), audit,
                  draft.revisionHistory(), true, draft.warnings()
                )
              );
            }
            return reviseDraft(bookId, draft.chapterContent(), audit);
          });
      });
  }

  // ── Write next chapter (full pipeline) ───────────────────────────────

  public CompletableFuture<WriteChapterOutput> writeNextChapter(String bookId) {
    var ctx = buildContext(bookId);

    return stateManager.listChapters(bookId)
      .thenApply(chapters -> {
        var nextNum = chapters.size() + 1;
        return nextNum;
      })
      .thenCompose(chapterNumber ->
        planChapter(bookId, chapterNumber)
          .thenCompose(plan -> composeChapter(bookId, chapterNumber, plan))
          .thenCompose(output -> {
            if (output.accepted()) {
              return stateManager.writeChapter(
                bookId, chapterNumber, output.chapterContent()
              ).thenApply(v -> output);
            }
            return CompletableFuture.completedFuture(output);
          })
      );
  }

  // ── Full draft pipeline (multi-chapter) ──────────────────────────────

  public CompletableFuture<List<WriteChapterOutput>> writeDraftPipeline(
    String bookId, int chapterCount
  ) {
    var results = new ArrayList<WriteChapterOutput>();
    return executeSequential(bookId, chapterCount, results, 0);
  }

  private CompletableFuture<List<WriteChapterOutput>> executeSequential(
    String bookId, int total, List<WriteChapterOutput> results, int done
  ) {
    if (done >= total) {
      return CompletableFuture.completedFuture(Collections.unmodifiableList(results));
    }
    return writeNextChapter(bookId)
      .thenCompose(output -> {
        results.add(output);
        return executeSequential(bookId, total, results, done + 1);
      });
  }

  // ── Radar ────────────────────────────────────────────────────────────

  public CompletableFuture<String> runRadar() {
    var ctx = buildContext(null);
    var input = AgentInput.builder()
      .prompt("（需要提供排行榜数据）")
      .systemPrompt(AgentPromptBuilder.radarSystemPrompt())
      .build();

    return agentRegistry.getAgent("radar")
      .execute(ctx, input)
      .thenApply(result -> {
        if (!result.success()) {
          return "{\"error\": \"" + result.error() + "\"}";
        }
        return result.content();
      });
  }

  // ── Fanfic book init ─────────────────────────────────────────────────

  public CompletableFuture<Void> initFanficBook(
    String bookId,
    String title,
    String language,
    String fandom,
    String sourceMaterial,
    boolean autoArchitect
  ) {
    var ctx = buildContext(bookId);
    return stateManager.ensureControlDocuments(bookId,
      "Fanfic based on " + fandom)
      .thenCompose(v -> {
        if (!autoArchitect) {
          return CompletableFuture.completedFuture(null);
        }

        var profile = new BookProfile(
          bookId, 0L, title, BookProfile.PLATFORM_TOMATO,
          "fanfic", BookProfile.STATUS_OUTLINING,
          200, 3000, language, "derivative",
          0, sourceMaterial, null, null, null, null
        );

        var extra = buildExtra(Map.of("profile", profile));
        var input = AgentInput.builder()
          .prompt("Import foundation source:\n" + sourceMaterial)
          .systemPrompt(AgentPromptBuilder.architectSystemPrompt(profile))
          .extra(extra)
          .build();

        return agentRegistry.getAgent("architect")
          .execute(ctx, input)
          .thenApply(result -> {
            if (!result.success()) {
              throw new RuntimeException("Fanfic architect failed: " + result.error());
            }
            return null;
          });
      });
  }

  // ── Helpers ──────────────────────────────────────────────────────────

  private BookProfile resolveProfile(String bookId) {
    return new BookProfile(
      bookId, 0L, bookId, BookProfile.PLATFORM_TOMATO,
      "xuanhuan", BookProfile.STATUS_ACTIVE,
      200, 3000, BookProfile.LANGUAGE_ZH, null, 0, "", null,
      null, null, null
    );
  }

  private List<String> parseAuditIssues(String auditContent) {
    var issues = new ArrayList<String>();
    try {
      var jsonMatch = auditContent.replaceAll("(?s).*?\\{", "{");
      var endIdx = jsonMatch.lastIndexOf("}");
      if (endIdx > 0) {
        jsonMatch = jsonMatch.substring(0, endIdx + 1);
      }
    } catch (Exception ignored) {}
    return issues;
  }
}
