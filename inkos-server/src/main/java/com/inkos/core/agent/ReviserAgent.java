package com.inkos.core.agent;

import com.inkos.core.pipeline.AgentContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ReviserAgent extends BaseAgent {

  public static final String DEFAULT_REVISE_MODE = "auto";
  public static final String MODE_SPOT_FIX = "spot-fix";
  public static final String MODE_POLISH = "polish";
  public static final String MODE_REWRITE = "rewrite";
  public static final String MODE_REWORK = "rework";
  public static final String MODE_ANTI_DETECT = "anti-detect";

  @Override
  public String getName() {
    return "reviser";
  }

  @Override
  public String getRole() {
    return "修订者 — 根据审计报告对章节进行定点修正";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    int chapterNumber = (int) extra.getOrDefault("chapterNumber", 0);
    String chapterContent = input.prompt();
    String reviseMode = (String) extra.getOrDefault("reviseMode", DEFAULT_REVISE_MODE);

    @SuppressWarnings("unchecked")
    var issues = (List<AgentResult.AuditIssue>) extra.getOrDefault("issues", List.of());

    String systemPrompt = AgentPromptBuilder.reviserSystemPrompt();
    String userPrompt = AgentPromptBuilder.reviserUserPrompt(chapterNumber, chapterContent, issues, reviseMode);

    return executeLlm(ctx, systemPrompt, userPrompt, 0.4, 16384)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of("agent", getName(), "chapterNumber", chapterNumber, "mode", reviseMode)
          );
        }
        return result;
      });
  }
}
