package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "truth_file")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TruthFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false, length = 36)
    private String bookId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "content_type", length = 50)
    private String contentType;

    @Builder.Default
    private Integer version = 1;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
