package com.inkos.core.model;

import jakarta.validation.constraints.Positive;

record LengthSpec(
    @Positive int target,
    @Positive int softMin,
    @Positive int softMax,
    @Positive int hardMin,
    @Positive int hardMax,
    String countingMode,
    String normalizeMode
) {
    public static final String COUNT_MODE_ZH = "zh_chars";
    public static final String COUNT_MODE_EN = "en_words";

    public static final String NORMALIZE_EXPAND = "expand";
    public static final String NORMALIZE_COMPRESS = "compress";
    public static final String NORMALIZE_NONE = "none";

    public boolean isOutsideHardRange(int count) {
        return count < hardMin || count > hardMax;
    }
}

record LengthTelemetry(
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

record LengthWarning(
    int chapter,
    int target,
    int actual,
    String countingMode,
    String reason
) {}
