package com.inkos.core.agent;

import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NormalizerAgent extends BaseAgent {

  @Override
  public String getName() {
    return "normalizer";
  }

  @Override
  public String getRole() {
    return "规范化器 — 调整章节长度至目标区间，规范化输出格式";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    var extra = input.extra();
    int targetLength = (int) extra.getOrDefault("targetLength", 3000);
    String mode = (String) extra.getOrDefault("mode", "none");

    if ("none".equals(mode)) {
      return CompletableFuture.completedFuture(
        AgentResult.ok(input.prompt(), Map.of("applied", false))
      );
    }

    String systemPrompt = AgentPromptBuilder.normalizerSystemPrompt();
    String userPrompt = AgentPromptBuilder.normalizerUserPrompt(input.prompt(), targetLength, mode);

    return executeLlm(ctx, systemPrompt, userPrompt, 0.2, 8192)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of("agent", getName(), "applied", true, "mode", mode)
          );
        }
        return result;
      });
  }
}
