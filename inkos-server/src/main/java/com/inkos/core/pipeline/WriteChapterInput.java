package com.inkos.core.pipeline;

import java.util.Map;

/**
 * Input record for the write-chapter operation.
 * Carries the chapter plan, author intent, style guide, character context, and runtime state facts.
 */
public record WriteChapterInput(
  String bookId,
  int chapterNumber,
  String chapterTitle,
  String planJson,
  String authorIntent,
  String currentFocus,
  String styleGuide,
  Map<String, String> characterContext,
  String runtimeStateDir,
  String previousChapterSummary,
  String contextPackage
) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String bookId;
    private int chapterNumber;
    private String chapterTitle;
    private String planJson;
    private String authorIntent;
    private String currentFocus;
    private String styleGuide;
    private Map<String, String> characterContext = Map.of();
    private String runtimeStateDir;
    private String previousChapterSummary;
    private String contextPackage;

    public Builder bookId(String val) { this.bookId = val; return this; }
    public Builder chapterNumber(int val) { this.chapterNumber = val; return this; }
    public Builder chapterTitle(String val) { this.chapterTitle = val; return this; }
    public Builder planJson(String val) { this.planJson = val; return this; }
    public Builder authorIntent(String val) { this.authorIntent = val; return this; }
    public Builder currentFocus(String val) { this.currentFocus = val; return this; }
    public Builder styleGuide(String val) { this.styleGuide = val; return this; }
    public Builder characterContext(Map<String, String> val) { this.characterContext = val; return this; }
    public Builder runtimeStateDir(String val) { this.runtimeStateDir = val; return this; }
    public Builder previousChapterSummary(String val) { this.previousChapterSummary = val; return this; }
    public Builder contextPackage(String val) { this.contextPackage = val; return this; }

    public WriteChapterInput build() {
      return new WriteChapterInput(bookId, chapterNumber, chapterTitle, planJson, authorIntent,
        currentFocus, styleGuide, characterContext, runtimeStateDir, previousChapterSummary, contextPackage);
    }
  }
}
