package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuditorAgent extends BaseAgent {

  @Override
  public String getName() {
    return "auditor";
  }

  @Override
  public String getRole() {
    return "审计员 — 全面审计章节质量（OOC、时间线、设定冲突、节奏、文风等32个维度）";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    var profile = (BookProfile) extra.get("profile");
    int chapterNumber = (int) extra.getOrDefault("chapterNumber", 0);
    String chapterContent = input.prompt();
    String storyBible = (String) extra.getOrDefault("storyBible", "");
    String characterMatrix = (String) extra.getOrDefault("characterMatrix", "");
    String pendingHooks = (String) extra.getOrDefault("pendingHooks", "");

    String systemPrompt = AgentPromptBuilder.auditorSystemPrompt(profile);
    String userPrompt = AgentPromptBuilder.auditorUserPrompt(
      chapterNumber, chapterContent, storyBible, characterMatrix, pendingHooks
    );

    return executeLlm(ctx, systemPrompt, userPrompt, 0.3, 8192)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of("agent", getName(), "chapterNumber", chapterNumber)
          );
        }
        return result;
      });
  }
}
