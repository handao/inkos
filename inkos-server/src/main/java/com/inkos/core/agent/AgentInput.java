package com.inkos.core.agent;

import java.util.Map;
import java.util.function.Consumer;

public record AgentInput(
  String prompt,
  String systemPrompt,
  String model,
  Double temperature,
  Integer maxTokens,
  Map<String, Object> extra,
  Consumer<String> streamCallback
) {
  public static Builder builder() {
    return new Builder();
  }

  public static AgentInput of(String prompt, String systemPrompt) {
    return new AgentInput(prompt, systemPrompt, null, null, null, Map.of(), null);
  }

  public static class Builder {
    private String prompt;
    private String systemPrompt;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private Map<String, Object> extra = Map.of();
    private Consumer<String> streamCallback;

    public Builder prompt(String val) { this.prompt = val; return this; }
    public Builder systemPrompt(String val) { this.systemPrompt = val; return this; }
    public Builder model(String val) { this.model = val; return this; }
    public Builder temperature(Double val) { this.temperature = val; return this; }
    public Builder maxTokens(Integer val) { this.maxTokens = val; return this; }
    public Builder extra(Map<String, Object> val) { this.extra = val; return this; }
    public Builder streamCallback(Consumer<String> val) { this.streamCallback = val; return this; }

    public AgentInput build() {
      return new AgentInput(prompt, systemPrompt, model, temperature, maxTokens, extra, streamCallback);
    }
  }
}
