package com.inkos.core.model;

import java.util.List;

public sealed interface AgentMessage {
    record SystemMessage(String content) implements AgentMessage {}
    record UserMessage(String content) implements AgentMessage {}
    record AssistantMessage(String content, List<ToolCall> toolCalls) implements AgentMessage {
        public AssistantMessage(String content) {
            this(content, List.of());
        }
    }
    record ToolResultMessage(String toolCallId, String content) implements AgentMessage {}

    record ToolCall(String id, String name, String arguments) {}
    record ToolDefinition(String name, String description, java.util.Map<String, Object> parameters) {}

    record LlmResponse(String content, TokenUsage usage) {}
    record TokenUsage(int promptTokens, int completionTokens, int totalTokens) {}

    record ChatWithToolsResult(String content, List<ToolCall> toolCalls) {}

    record StreamProgress(
        long elapsedMs,
        int totalChars,
        int chineseChars,
        String status
    ) {
        public static final String STATUS_STREAMING = "streaming";
        public static final String STATUS_DONE = "done";
    }
}
