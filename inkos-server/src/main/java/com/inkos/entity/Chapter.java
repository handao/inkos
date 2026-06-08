package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapter")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false, length = 36)
    private String bookId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "word_count")
    @Builder.Default
    private Integer wordCount = 0;

    @Column(length = 20)
    @Builder.Default
    private String status = "draft";

    @Builder.Default
    private Integer version = 1;

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
