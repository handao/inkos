package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "llm_service_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LlmServiceConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;

    @Column(length = 100)
    private String label;

    @Column(name = "base_url", length = 500)
    private String baseUrl;

    @Column(name = "api_type", length = 20)
    private String apiType;

    @Column(columnDefinition = "JSON")
    private String models;

    @Column(name = "default_model", length = 100)
    private String defaultModel;

    @Column(name = "is_cover_provider")
    @Builder.Default
    private Boolean isCoverProvider = false;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
