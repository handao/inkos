package com.inkos.core.llm;

import java.util.List;

public record LlmResponse(
    String content,
    Usage usage,
    List<ToolCall> toolCalls
) {
    public record Usage(
        int promptTokens,
        int completionTokens,
        int totalTokens
    ) {
        public static Usage sum(Usage a, Usage b) {
            return new Usage(
                a.promptTokens + b.promptTokens,
                a.completionTokens + b.completionTokens,
                a.totalTokens + b.totalTokens
            );
        }
    }

    public record ToolCall(
        String id,
        String name,
        String arguments
    ) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;
        private Usage usage;
        private List<ToolCall> toolCalls = List.of();

        public Builder content(String content) { this.content = content; return this; }
        public Builder usage(Usage usage) { this.usage = usage; return this; }
        public Builder toolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; return this; }

        public LlmResponse build() {
            return new LlmResponse(content, usage, toolCalls);
        }
    }
}
