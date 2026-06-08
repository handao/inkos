package com.inkos.dto.response;
import com.inkos.entity.AgentSession;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class SessionResponse {
    private final String sessionId;
    private final String bookId;
    private final String title;
    private final String mode;
    private final boolean isDraft;
    private final boolean isStreaming;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static SessionResponse from(AgentSession session) {
        return SessionResponse.builder()
                .sessionId(session.getSessionId()).bookId(session.getBookId())
                .title(session.getTitle()).mode(session.getMode())
                .isDraft(session.getIsDraft() != null && session.getIsDraft())
                .isStreaming(session.getIsStreaming() != null && session.getIsStreaming())
                .createdAt(session.getCreatedAt()).updatedAt(session.getUpdatedAt()).build();
    }
}
