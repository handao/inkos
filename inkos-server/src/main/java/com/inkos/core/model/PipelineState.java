package com.inkos.core.model;

import java.util.List;

public record PipelineState(
    int chapter,
    String location,
    Protagonist protagonist,
    List<Enemy> enemies,
    List<String> knownTruths,
    String currentConflict,
    String anchor
) {
    public record Protagonist(
        String status,
        String currentGoal,
        String constraints
    ) {}

    public record Enemy(
        String name,
        String relationship,
        String threat
    ) {}

    public record LedgerEntry(
        int chapter,
        int openingValue,
        String source,
        String resourceCompleteness,
        int delta,
        int closingValue,
        String basis
    ) {}

    public record ParticleLedger(
        int hardCap,
        int currentTotal,
        List<LedgerEntry> entries
    ) {}

    public record PendingHook(
        String id,
        int originChapter,
        String type,
        String status,
        String lastProgress,
        String expectedResolution,
        String note
    ) {
        public static final String STATUS_OPEN = "open";
        public static final String STATUS_PROGRESSING = "progressing";
        public static final String STATUS_RESOLVED = "resolved";
    }
}
