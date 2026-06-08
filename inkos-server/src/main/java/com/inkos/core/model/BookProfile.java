package com.inkos.core.model;

import java.time.LocalDateTime;

public record BookProfile(
    String id,
    long userId,
    String title,
    String platform,
    String genre,
    String status,
    int targetChapters,
    int chapterWordCount,
    String language,
    String fanficMode,
    int chaptersWritten,
    String outline,
    String coverImageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String parentBookId
) {
    public static final String STATUS_INCUBATING = "incubating";
    public static final String STATUS_OUTLINING = "outlining";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_PAUSED = "paused";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_DROPPED = "dropped";

    public static final String PLATFORM_TOMATO = "tomato";
    public static final String PLATFORM_FEILU = "feilu";
    public static final String PLATFORM_QIDIAN = "qidian";
    public static final String PLATFORM_OTHER = "other";

    public static final String LANGUAGE_ZH = "zh";
    public static final String LANGUAGE_EN = "en";

    public static String normalizePlatform(String raw) {
        if (raw == null) return PLATFORM_OTHER;
        String lowered = raw.trim().toLowerCase().replaceAll("[\\s_\\-]+", "");
        if (lowered.contains("tomato") || lowered.contains("fanqie") || raw.contains("番茄"))
            return PLATFORM_TOMATO;
        if (lowered.contains("qidian") || raw.contains("起点"))
            return PLATFORM_QIDIAN;
        if (lowered.contains("feilu") || raw.contains("飞卢"))
            return PLATFORM_FEILU;
        return PLATFORM_OTHER;
    }
}
