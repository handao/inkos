package com.inkos.core.agent;

import com.inkos.core.llm.LlmProvider;
import com.inkos.core.llm.LlmRequest;
import com.inkos.core.llm.LlmResponse;
import com.inkos.core.pipeline.AgentContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseAgentTest {

  private BaseAgent agent;
  private LlmProvider llmProvider;
  private AgentContext ctx;

  @BeforeEach
  void setUp() {
    llmProvider = mock(LlmProvider.class);
    ctx = mock(AgentContext.class);
    when(ctx.model()).thenReturn("test-model");
    when(ctx.llmProvider()).thenReturn(llmProvider);

    agent = new BaseAgent() {
      @Override public String getName() { return "test-agent"; }
      @Override public String getRole() { return "test role"; }
      @Override public CompletableFuture<AgentResult> execute(AgentContext ctx, AgentInput input) {
        return CompletableFuture.completedFuture(AgentResult.ok("test"));
      }
    };
  }

  @Test
  void callLlm_shouldCreateRequestWithDefaultParams() {
    var response = LlmResponse.builder()
      .content("response content")
      .usage(new LlmResponse.Usage(10, 20, 30))
      .build();
    when(llmProvider.chat(any())).thenReturn(CompletableFuture.completedFuture(response));

    agent.callLlm(ctx, "system prompt", "user prompt").join();

    var captor = ArgumentCaptor.forClass(LlmRequest.class);
    verify(llmProvider).chat(captor.capture());
    var req = captor.getValue();

    assertAll(
      () -> assertEquals("test-model", req.model()),
      () -> assertEquals(0.7, req.temperature()),
      () -> assertEquals(4096, req.maxTokens()),
      () -> assertFalse(req.stream()),
      () -> assertEquals(2, req.messages().size()),
      () -> assertEquals("system", req.messages().get(0).role()),
      () -> assertEquals("system prompt", req.messages().get(0).content()),
      () -> assertEquals("user", req.messages().get(1).role()),
      () -> assertEquals("user prompt", req.messages().get(1).content())
    );
  }

  @Test
  void callLlm_shouldCreateRequestWithCustomParams() {
    var response = LlmResponse.builder()
      .content("response")
      .usage(new LlmResponse.Usage(5, 15, 20))
      .build();
    when(llmProvider.chat(any())).thenReturn(CompletableFuture.completedFuture(response));

    agent.callLlm(ctx, "sys", "usr", 0.3, 2048).join();

    var captor = ArgumentCaptor.forClass(LlmRequest.class);
    verify(llmProvider).chat(captor.capture());
    var req = captor.getValue();

    assertAll(
      () -> assertEquals("test-model", req.model()),
      () -> assertEquals(0.3, req.temperature()),
      () -> assertEquals(2048, req.maxTokens()),
      () -> assertFalse(req.stream())
    );
  }

  @Test
  void executeLlm_shouldReturnOkOnSuccess() {
    var usage = new LlmResponse.Usage(100, 200, 300);
    var response = LlmResponse.builder()
      .content("chapter content")
      .usage(usage)
      .build();
    when(llmProvider.chat(any())).thenReturn(CompletableFuture.completedFuture(response));

    AgentResult result = agent.executeLlm(ctx, "system", "user").join();

    assertAll(
      () -> assertTrue(result.success()),
      () -> assertEquals("chapter content", result.content()),
      () -> assertEquals(usage, result.metadata().get("tokens"))
    );
  }

  @Test
  void executeLlm_shouldReturnFailedOnException() {
    when(llmProvider.chat(any())).thenReturn(
      CompletableFuture.failedFuture(new RuntimeException("API call failed"))
    );

    AgentResult result = agent.executeLlm(ctx, "system", "user").join();

    assertAll(
      () -> assertFalse(result.success()),
      () -> assertNull(result.content()),
      () -> assertTrue(result.error().contains("API call failed"))
    );
  }

  @Test
  void executeLlm_shouldUseDefaultParams() {
    var response = LlmResponse.builder()
      .content("content")
      .usage(new LlmResponse.Usage(1, 1, 2))
      .build();
    when(llmProvider.chat(any())).thenReturn(CompletableFuture.completedFuture(response));

    agent.executeLlm(ctx, "sys", "usr").join();

    var captor = ArgumentCaptor.forClass(LlmRequest.class);
    verify(llmProvider).chat(captor.capture());
    assertEquals(0.7, captor.getValue().temperature());
    assertEquals(4096, captor.getValue().maxTokens());
  }

  @Test
  void executeLlm_shouldUseCustomParams() {
    var response = LlmResponse.builder()
      .content("content")
      .usage(new LlmResponse.Usage(1, 1, 2))
      .build();
    when(llmProvider.chat(any())).thenReturn(CompletableFuture.completedFuture(response));

    agent.executeLlm(ctx, "sys", "usr", 0.5, 8192).join();

    var captor = ArgumentCaptor.forClass(LlmRequest.class);
    verify(llmProvider).chat(captor.capture());
    assertAll(
      () -> assertEquals(0.5, captor.getValue().temperature()),
      () -> assertEquals(8192, captor.getValue().maxTokens())
    );
  }

  @Test
  void callLlm_shouldPropagateModelFromContext() {
    when(ctx.model()).thenReturn("custom-model");
    var response = LlmResponse.builder()
      .content("r")
      .usage(new LlmResponse.Usage(0, 0, 0))
      .build();
    when(llmProvider.chat(any())).thenReturn(CompletableFuture.completedFuture(response));

    agent.callLlm(ctx, "sys", "usr").join();

    var captor = ArgumentCaptor.forClass(LlmRequest.class);
    verify(llmProvider).chat(captor.capture());
    assertEquals("custom-model", captor.getValue().model());
  }
}
