package com.inkos.core.model;

import java.util.List;

record DetectionHistoryEntry(
    int chapterNumber,
    String timestamp,
    String provider,
    double score,
    String action,
    int attempt
) {
    public static final String ACTION_DETECT = "detect";
    public static final String ACTION_REWRITE = "rewrite";
}

record DetectionStats(
    int totalDetections,
    int totalRewrites,
    double avgOriginalScore,
    double avgFinalScore,
    double avgScoreReduction,
    double passRate,
    List<ChapterBreakdown> chapterBreakdown
) {
    public record ChapterBreakdown(
        int chapterNumber,
        double originalScore,
        double finalScore,
        int rewriteAttempts
    ) {}
}
