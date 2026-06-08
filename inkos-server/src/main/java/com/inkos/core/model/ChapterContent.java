package com.inkos.core.model;

import java.time.LocalDateTime;
import java.util.List;

public record ChapterContent(
    int number,
    String title,
    String status,
    int wordCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<String> auditIssues,
    List<String> lengthWarnings,
    String reviewNote,
    Double detectionScore,
    String detectionProvider,
    LocalDateTime detectedAt,
    LengthTelemetry lengthTelemetry,
    TokenUsage tokenUsage
) {
    public static final String STATUS_CARD_GENERATED = "card-generated";
    public static final String STATUS_DRAFTING = "drafting";
    public static final String STATUS_DRAFTED = "drafted";
    public static final String STATUS_AUDITING = "auditing";
    public static final String STATUS_AUDIT_PASSED = "audit-passed";
    public static final String STATUS_AUDIT_FAILED = "audit-failed";
    public static final String STATUS_STATE_DEGRADED = "state-degraded";
    public static final String STATUS_REVISING = "revising";
    public static final String STATUS_READY_FOR_REVIEW = "ready-for-review";
    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_IMPORTED = "imported";

    public record LengthTelemetry(
        int target,
        int softMin,
        int softMax,
        int hardMin,
        int hardMax,
        String countingMode,
        int writerCount,
        int postWriterNormalizeCount,
        int postReviseCount,
        int finalCount,
        boolean normalizeApplied,
        boolean lengthWarning
    ) {}

    public record TokenUsage(
        int promptTokens,
        int completionTokens,
        int totalTokens
    ) {
        public static TokenUsage sum(TokenUsage a, TokenUsage b) {
            return new TokenUsage(
                a.promptTokens + b.promptTokens,
                a.completionTokens + b.completionTokens,
                a.totalTokens + b.totalTokens
            );
        }
    }
}
