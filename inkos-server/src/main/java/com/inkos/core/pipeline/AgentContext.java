package com.inkos.core.pipeline;

import com.inkos.core.llm.LlmProvider;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Shared context passed to all pipeline agents.
 * Carries the LLM provider, model selection, project root, and a streaming progress callback.
 */
public record AgentContext(
  LlmProvider llmProvider,
  String model,
  Path projectRoot,
  String bookId,
  Consumer<String> onStreamProgress
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private LlmProvider llmProvider;
    private String model;
    private Path projectRoot;
    private String bookId;
    private Consumer<String> onStreamProgress;

    public Builder llmProvider(LlmProvider val) { this.llmProvider = val; return this; }
    public Builder model(String val) { this.model = val; return this; }
    public Builder projectRoot(Path val) { this.projectRoot = val; return this; }
    public Builder bookId(String val) { this.bookId = val; return this; }
    public Builder onStreamProgress(Consumer<String> val) { this.onStreamProgress = val; return this; }

    public AgentContext build() {
      return new AgentContext(llmProvider, model, projectRoot, bookId, onStreamProgress);
    }
  }
}
