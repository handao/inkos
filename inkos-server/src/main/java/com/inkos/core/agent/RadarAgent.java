package com.inkos.core.agent;

import com.inkos.core.pipeline.AgentContext;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RadarAgent extends BaseAgent {

  @Override
  public String getName() {
    return "radar";
  }

  @Override
  public String getRole() {
    return "市场雷达 — 扫描网文市场趋势，识别热门题材和机会点";
  }

  @Override
  public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
    String rankings = input.prompt();
    String systemPrompt = AgentPromptBuilder.radarSystemPrompt();
    String userPrompt = AgentPromptBuilder.radarUserPrompt(rankings);

    return executeLlm(ctx, systemPrompt, userPrompt, 0.6, 4096)
      .thenApply(result -> {
        if (result.success()) {
          return AgentResult.ok(
            result.content(),
            Map.of("agent", getName(), "timestamp", System.currentTimeMillis())
          );
        }
        return result;
      });
  }
}
