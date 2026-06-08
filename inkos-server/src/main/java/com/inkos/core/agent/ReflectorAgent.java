package com.inkos.core.agent;

import com.inkos.core.model.BookProfile;
import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ReflectorAgent extends BaseAgent {

  @Override
  public String getName() {
    return "reflector";
  }

  @Override
  public String getRole() {
    return "反射器 — 将观察者提取的新事实合并更新到真相文件中";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    var profile = (BookProfile) extra.get("profile");
    int chapterNumber = (int) extra.getOrDefault("chapterNumber", 0);
    String observations = input.prompt();
    String currentState = (String) extra.getOrDefault("currentState", "");
    String ledger = (String) extra.getOrDefault("ledger", "");
    String pendingHooks = (String) extra.getOrDefault("pendingHooks", "");

    String systemPrompt = AgentPromptBuilder.reflectorSystemPrompt(profile);
    String userPrompt = AgentPromptBuilder.reflectorUserPrompt(
      chapterNumber, observations, currentState, ledger, pendingHooks
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
