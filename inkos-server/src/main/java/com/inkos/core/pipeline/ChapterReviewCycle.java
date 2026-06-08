package com.inkos.core.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkos.core.agent.*;
import com.inkos.core.llm.LlmProvider;
import com.inkos.core.model.BookProfile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Review cycle logic: audit → revise → re-audit loop until passing or max cycles reached.
 *
 * TS reference: packages/core/src/pipeline/chapter-review-cycle.ts
 * Cycles through: ContinuityAuditor.audit() → ReviserAgent.revise() → ContinuityAuditor.audit()
 */
public class ChapterReviewCycle {

  private static final ObjectMapper JSON = new ObjectMapper();

  private final LlmProvider llmProvider;
  private final String model;
  private final int maxCycles;
  private final AgentRegistry agentRegistry;
  private final Path projectRoot;

  public ChapterReviewCycle(LlmProvider llmProvider, String model, int maxCycles) {
    this(llmProvider, model, maxCycles, new AgentRegistry(), null);
  }

  public ChapterReviewCycle(
    LlmProvider llmProvider,
    String model,
    int maxCycles,
    AgentRegistry agentRegistry,
    Path projectRoot
  ) {
    this.llmProvider = llmProvider;
    this.model = model;
    this.maxCycles = maxCycles;
    this.agentRegistry = agentRegistry;
    this.projectRoot = projectRoot;
  }

  /**
   * Run the full audit → revise loop.
   * Returns the final accepted content and revision history.
   *
   * TS: runChapterReviewCycle() in chapter-review-cycle.ts
   *   1. Initial audit via ContinuityAuditor
   *   2. If issues found, build revision prompt
   *   3. Call ReviserAgent.revise() in specified mode
   *   4. Re-audit revised content
   *   5. Repeat until clean or maxCycles reached
   *   6. Also runs validateChapterTruthPersistence() after acceptance
   */
  public ReviewResult execute(String bookId, String chapterContent) {
    var ctx = AgentContext.builder()
      .llmProvider(llmProvider)
      .model(model)
      .projectRoot(projectRoot)
      .bookId(bookId)
      .build();

    var profile = resolveProfile(bookId);
    var revisionLog = new ArrayList<String>();
    String currentContent = chapterContent;

    var auditExtra = Map.<String, Object>of(
      "profile", profile,
      "storyBible", "",
      "characterMatrix", "",
      "pendingHooks", ""
    );

    // Initial audit
    var auditorInput = AgentInput.builder()
      .prompt(currentContent)
      .systemPrompt(AgentPromptBuilder.auditorSystemPrompt(profile))
      .extra(auditExtra)
      .build();

    var auditorResult = agentRegistry.getAgent("auditor").execute(ctx, auditorInput).join();
    if (!auditorResult.success()) {
      revisionLog.add("Initial audit failed: " + auditorResult.error());
      return new ReviewResult(currentContent, false, 0, revisionLog);
    }

    if (isAuditPassed(auditorResult.content())) {
      revisionLog.add("Initial audit passed");
      return new ReviewResult(currentContent, true, 0, revisionLog);
    }

    revisionLog.add("Initial audit found issues");

    // Review cycles
    for (int cycle = 0; cycle < maxCycles; cycle++) {
      var issues = parseAuditIssues(auditorResult.content());

      var reviseExtra = Map.<String, Object>of(
        "issues", issues,
        "reviseMode", ReviserAgent.DEFAULT_REVISE_MODE,
        "chapterNumber", 0
      );

      var reviserInput = AgentInput.builder()
        .prompt(currentContent)
        .systemPrompt(AgentPromptBuilder.reviserSystemPrompt())
        .extra(reviseExtra)
        .build();

      var reviserResult = agentRegistry.getAgent("reviser").execute(ctx, reviserInput).join();
      if (!reviserResult.success()) {
        revisionLog.add("Cycle " + (cycle + 1) + " revise failed: " + reviserResult.error());
        break;
      }

      String revisedContent = reviserResult.content();
      revisionLog.add("Cycle " + (cycle + 1) + ": revised");

      // Re-audit the revised content
      var reAuditInput = AgentInput.builder()
        .prompt(revisedContent)
        .systemPrompt(AgentPromptBuilder.auditorSystemPrompt(profile))
        .extra(auditExtra)
        .build();

      auditorResult = agentRegistry.getAgent("auditor").execute(ctx, reAuditInput).join();
      if (!auditorResult.success()) {
        revisionLog.add("Cycle " + (cycle + 1) + " re-audit failed: " + auditorResult.error());
        currentContent = revisedContent;
        break;
      }

      if (isAuditPassed(auditorResult.content())) {
        revisionLog.add("Cycle " + (cycle + 1) + ": passed after revision");
        return new ReviewResult(revisedContent, true, cycle + 1, revisionLog);
      }

      currentContent = revisedContent;
      revisionLog.add("Cycle " + (cycle + 1) + ": still has issues, continuing");
    }

    return new ReviewResult(currentContent, false, revisionLog.size(), revisionLog);
  }

  private boolean isAuditPassed(String auditJson) {
    try {
      JsonNode root = JSON.readTree(auditJson);
      JsonNode passed = root.get("passed");
      return passed != null && passed.asBoolean();
    } catch (Exception e) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  private List<AgentResult.AuditIssue> parseAuditIssues(String auditJson) {
    try {
      JsonNode root = JSON.readTree(auditJson);
      JsonNode issues = root.get("issues");
      if (issues != null && issues.isArray()) {
        var result = new ArrayList<AgentResult.AuditIssue>();
        for (JsonNode issue : issues) {
          result.add(new AgentResult.AuditIssue(
            issue.has("severity") ? issue.get("severity").asText("warning") : "warning",
            issue.has("category") ? issue.get("category").asText("audit") : "audit",
            issue.has("description") ? issue.get("description").asText("") : "",
            issue.has("suggestion") ? issue.get("suggestion").asText("") : ""
          ));
        }
        return result;
      }
    } catch (Exception ignored) {}
    return List.of();
  }

  private BookProfile resolveProfile(String bookId) {
    return new BookProfile(
      bookId, 0L, bookId, BookProfile.PLATFORM_TOMATO,
      "xuanhuan", BookProfile.STATUS_ACTIVE,
      200, 3000, BookProfile.LANGUAGE_ZH, null, 0, "", null,
      null, null, null
    );
  }

  public record ReviewResult(
    String finalContent,
    boolean accepted,
    int cyclesUsed,
    List<String> revisionLog
  ) {}
}
