package com.inkos.dto.response;
import com.inkos.entity.AgentMessage;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class MessageResponse {
    private final Long id;
    private final String sessionId;
    private final String role;
    private final String content;
    private final String toolCalls;
    private final int sortOrder;
    private final LocalDateTime createdAt;

    public static MessageResponse from(AgentMessage message) {
        return MessageResponse.builder()
                .id(message.getId()).sessionId(message.getSessionId())
                .role(message.getRole()).content(message.getContent())
                .toolCalls(message.getToolCalls())
                .sortOrder(message.getSortOrder())
                .createdAt(message.getCreatedAt()).build();
    }
}
