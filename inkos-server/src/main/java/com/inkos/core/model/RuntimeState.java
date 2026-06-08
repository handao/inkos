package com.inkos.core.model;

import java.util.List;

public record RuntimeState(
    StateManifest manifest,
    HooksState hooks,
    ChapterSummariesState chapterSummaries,
    CurrentStateState currentState
) {
    public record StateManifest(
        int schemaVersion,
        String language,
        int lastAppliedChapter,
        int projectionVersion,
        List<String> migrationWarnings
    ) {
        public static final int SCHEMA_VERSION = 2;
    }

    public record HookRecord(
        String hookId,
        int startChapter,
        String type,
        String status,
        int lastAdvancedChapter,
        String expectedPayoff,
        String payoffTiming,
        String notes,
        List<String> dependsOn,
        String paysOffInArc,
        Boolean coreHook,
        Integer halfLifeChapters,
        Integer advancedCount,
        Boolean promoted
    ) {
        public static final String STATUS_OPEN = "open";
        public static final String STATUS_PROGRESSING = "progressing";
        public static final String STATUS_DEFERRED = "deferred";
        public static final String STATUS_RESOLVED = "resolved";

        public static final String PAYOFF_IMMEDIATE = "immediate";
        public static final String PAYOFF_NEAR_TERM = "near-term";
        public static final String PAYOFF_MID_ARC = "mid-arc";
        public static final String PAYOFF_SLOW_BURN = "slow-burn";
        public static final String PAYOFF_ENDGAME = "endgame";
    }

    public record HooksState(
        List<HookRecord> hooks
    ) {}

    public record ChapterSummaryRow(
        int chapter,
        String title,
        String characters,
        String events,
        String stateChanges,
        String hookActivity,
        String mood,
        String chapterType
    ) {}

    public record ChapterSummariesState(
        List<ChapterSummaryRow> rows
    ) {}

    public record CurrentStateFact(
        String subject,
        String predicate,
        String object,
        int validFromChapter,
        Integer validUntilChapter,
        int sourceChapter
    ) {}

    public record CurrentStateState(
        int chapter,
        List<CurrentStateFact> facts
    ) {}

    public record CurrentStatePatch(
        String currentLocation,
        String protagonistState,
        String currentGoal,
        String currentConstraint,
        String currentAlliances,
        String currentConflict
    ) {}

    public record HookOps(
        List<HookRecord> upsert,
        List<String> mention,
        List<String> resolve,
        List<String> defer
    ) {}

    public record NewHookCandidate(
        String type,
        String expectedPayoff,
        String payoffTiming,
        String notes
    ) {}

    public record RuntimeStateDelta(
        int chapter,
        CurrentStatePatch currentStatePatch,
        HookOps hookOps,
        List<NewHookCandidate> newHookCandidates,
        ChapterSummaryRow chapterSummary,
        List<java.util.Map<String, Object>> subplotOps,
        List<java.util.Map<String, Object>> emotionalArcOps,
        List<java.util.Map<String, Object>> characterMatrixOps,
        List<String> notes
    ) {}
}
