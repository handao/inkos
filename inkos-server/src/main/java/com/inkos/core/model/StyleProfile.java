package com.inkos.core.model;

import java.util.List;

public record StyleProfile(
    double avgSentenceLength,
    double sentenceLengthStdDev,
    double avgParagraphLength,
    ParagraphLengthRange paragraphLengthRange,
    double vocabularyDiversity,
    List<String> topPatterns,
    List<String> rhetoricalFeatures,
    String sourceName,
    String analyzedAt
) {
    public record ParagraphLengthRange(
        int min,
        int max
    ) {}
}
