package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WriterAgent extends BaseAgent {

  @Override
  public String getName() {
    return "writer";
  }

  @Override
  public String getRole() {
    return "写手 — 根据章节计划和基础设定撰写章节正文";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    var profile = (BookProfile) extra.get("profile");
    int chapterNumber = (int) extra.getOrDefault("chapterNumber", 0);
    String chapterIntent = (String) extra.getOrDefault("chapterIntent", "");
    String storyState = (String) extra.getOrDefault("storyState", "");
    String storyBible = (String) extra.getOrDefault("storyBible", "");
    String recentChapters = (String) extra.getOrDefault("recentChapters", "");
    String pendingHooks = (String) extra.getOrDefault("pendingHooks", "");
    String characterMatrix = (String) extra.getOrDefault("characterMatrix", "");

    String systemPrompt = AgentPromptBuilder.writerSystemPrompt(profile);
    String userPrompt = AgentPromptBuilder.writerUserPrompt(
      profile, chapterNumber, chapterIntent, storyState, storyBible,
      recentChapters, pendingHooks, characterMatrix
    );

    return executeLlm(ctx, systemPrompt, userPrompt, 0.7, 16384)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of(
              "agent", getName(),
              "chapterNumber", chapterNumber,
              "profile", profile.title()
            )
          );
        }
        return result;
      });
  }
}
