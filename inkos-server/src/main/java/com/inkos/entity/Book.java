package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 50)
    private String genre;

    @Column(length = 20)
    @Builder.Default
    private String status = "draft";

    @Column(length = 10)
    @Builder.Default
    private String language = "zh";

    @Column(name = "fanfic_mode", length = 50)
    private String fanficMode;

    @Column(name = "chapters_written")
    @Builder.Default
    private Integer chaptersWritten = 0;

    @Column(columnDefinition = "TEXT")
    private String outline;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

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
