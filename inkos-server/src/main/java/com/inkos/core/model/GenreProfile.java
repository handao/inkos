package com.inkos.core.model;

import java.util.List;

public record GenreProfile(
    String name,
    String id,
    String language,
    List<String> chapterTypes,
    List<String> fatigueWords,
    boolean numericalSystem,
    boolean powerScaling,
    boolean eraResearch,
    String pacingRule,
    List<String> satisfactionTypes,
    List<Double> auditDimensions
) {
    public static final String LANGUAGE_ZH = "zh";
    public static final String LANGUAGE_EN = "en";

    public static GenreProfile defaultProfile() {
        return new GenreProfile(
            "unknown",
            "unknown",
            LANGUAGE_ZH,
            List.of(),
            List.of(),
            false,
            false,
            false,
            "",
            List.of(),
            List.of()
        );
    }
}
