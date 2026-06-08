package com.inkos.core.agent;

import com.inkos.core.llm.LlmProvider;
import com.inkos.core.llm.LlmRequest;
import com.inkos.core.llm.LlmResponse;
import com.inkos.core.pipeline.AgentContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseAgent implements Agent {

  protected CompletableFuture<LlmResponse> callLlm(
    AgentContext ctx,
    String systemPrompt,
    String userPrompt
  ) {
    return callLlm(ctx, systemPrompt, userPrompt, 0.7, 4096);
  }

  protected CompletableFuture<LlmResponse> callLlm(
    AgentContext ctx,
    String systemPrompt,
    String userPrompt,
    double temperature,
    int maxTokens
  ) {
    var messages = List.of(
      new LlmRequest.Message(LlmRequest.Message.ROLE_SYSTEM, systemPrompt),
      new LlmRequest.Message(LlmRequest.Message.ROLE_USER, userPrompt)
    );

    var request = LlmRequest.builder()
      .model(ctx.model())
      .messages(messages)
      .temperature(temperature)
      .maxTokens(maxTokens)
      .stream(false)
      .build();

    return ctx.llmProvider().chat(request);
  }

  protected CompletableFuture<AgentResult> executeLlm(
    AgentContext ctx,
    String systemPrompt,
    String userPrompt
  ) {
    return executeLlm(ctx, systemPrompt, userPrompt, 0.7, 4096);
  }

  protected CompletableFuture<AgentResult> executeLlm(
    AgentContext ctx,
    String systemPrompt,
    String userPrompt,
    double temperature,
    int maxTokens
  ) {
    return callLlm(ctx, systemPrompt, userPrompt, temperature, maxTokens)
      .thenApply(response -> AgentResult.ok(
        response.content(),
        Map.of("tokens", response.usage())
      ))
      .exceptionally(e -> AgentResult.failed(e.getMessage()));
  }
}
