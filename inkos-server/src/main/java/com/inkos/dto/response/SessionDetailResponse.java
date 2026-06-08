package com.inkos.dto.response;

import com.inkos.entity.AgentSession;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class SessionDetailResponse {
    private final String sessionId;
    private final String bookId;
    private final String title;
    private final String mode;
    private final boolean isDraft;
    private final boolean isStreaming;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<MessageResponse> messages;

    public static SessionDetailResponse from(AgentSession session, List<MessageResponse> messages) {
        return SessionDetailResponse.builder()
                .sessionId(session.getSessionId()).bookId(session.getBookId())
                .title(session.getTitle()).mode(session.getMode())
                .isDraft(session.getIsDraft() != null && session.getIsDraft())
                .isStreaming(session.getIsStreaming() != null && session.getIsStreaming())
                .createdAt(session.getCreatedAt()).updatedAt(session.getUpdatedAt())
                .messages(messages).build();
    }
}
