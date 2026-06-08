package com.inkos.core.llm;

import java.util.List;
import java.util.Map;

public record LlmRequest(
    String model,
    List<Message> messages,
    List<ToolDefinition> tools,
    double temperature,
    int maxTokens,
    boolean stream,
    Map<String, Object> extra
) {
    public record Message(
        String role,
        String content
    ) {
        public static final String ROLE_SYSTEM = "system";
        public static final String ROLE_USER = "user";
        public static final String ROLE_ASSISTANT = "assistant";
    }

    public record ToolDefinition(
        String name,
        String description,
        Map<String, Object> parameters
    ) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String model;
        private List<Message> messages;
        private List<ToolDefinition> tools = List.of();
        private double temperature = 0.7;
        private int maxTokens = 4096;
        private boolean stream = true;
        private Map<String, Object> extra = Map.of();

        public Builder model(String model) { this.model = model; return this; }
        public Builder messages(List<Message> messages) { this.messages = messages; return this; }
        public Builder tools(List<ToolDefinition> tools) { this.tools = tools; return this; }
        public Builder temperature(double temperature) { this.temperature = temperature; return this; }
        public Builder maxTokens(int maxTokens) { this.maxTokens = maxTokens; return this; }
        public Builder stream(boolean stream) { this.stream = stream; return this; }
        public Builder extra(Map<String, Object> extra) { this.extra = extra; return this; }

        public LlmRequest build() {
            return new LlmRequest(model, messages, tools, temperature, maxTokens, stream, extra);
        }
    }
}
