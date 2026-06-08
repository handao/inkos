package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_session")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgentSession {
    @Id
    @Column(name = "session_id", length = 36)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", length = 36)
    private String bookId;

    @Column(length = 200)
    private String title;

    @Column(length = 20)
    @Builder.Default
    private String mode = "chat";

    @Column(name = "is_draft")
    @Builder.Default
    private Boolean isDraft = true;

    @Column(name = "is_streaming")
    @Builder.Default
    private Boolean isStreaming = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
