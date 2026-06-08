package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ObserverAgent extends BaseAgent {

  @Override
  public String getName() {
    return "observer";
  }

  @Override
  public String getRole() {
    return "观察者 — 阅读章节正文，提取角色状态变化、伏笔动态、关系变化等新事实";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    var profile = (BookProfile) extra.get("profile");
    int chapterNumber = (int) extra.getOrDefault("chapterNumber", 0);
    String title = (String) extra.getOrDefault("chapterTitle", "");
    String chapterContent = input.prompt();

    String systemPrompt = AgentPromptBuilder.observerSystemPrompt(profile);
    String userPrompt = AgentPromptBuilder.observerUserPrompt(chapterNumber, title, chapterContent);

    return executeLlm(ctx, systemPrompt, userPrompt, 0.5, 8192)
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
