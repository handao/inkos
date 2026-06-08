package com.inkos.core.pipeline;

import java.util.List;

/**
 * Output record from the write-chapter operation.
 * Contains the generated chapter content, metadata, audit results, and revision history.
 */
public record WriteChapterOutput(
  String chapterContent,
  String chapterTitle,
  int chapterNumber,
  long totalTokens,
  long writeDurationMs,
  AuditResult auditResult,
  List<RevisionAttempt> revisionHistory,
  boolean accepted,
  List<String> warnings
) {

  public record AuditResult(
    boolean passed,
    int issueCount,
    List<String> issues,
    double continuityScore
  ) {}

  public record RevisionAttempt(
    int attemptNumber,
    String revisedContent,
    List<String> changes,
    long durationMs
  ) {}

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String chapterContent;
    private String chapterTitle;
    private int chapterNumber;
    private long totalTokens;
    private long writeDurationMs;
    private AuditResult auditResult;
    private List<RevisionAttempt> revisionHistory = List.of();
    private boolean accepted = true;
    private List<String> warnings = List.of();

    public Builder chapterContent(String val) { this.chapterContent = val; return this; }
    public Builder chapterTitle(String val) { this.chapterTitle = val; return this; }
    public Builder chapterNumber(int val) { this.chapterNumber = val; return this; }
    public Builder totalTokens(long val) { this.totalTokens = val; return this; }
    public Builder writeDurationMs(long val) { this.writeDurationMs = val; return this; }
    public Builder auditResult(AuditResult val) { this.auditResult = val; return this; }
    public Builder revisionHistory(List<RevisionAttempt> val) { this.revisionHistory = val; return this; }
    public Builder accepted(boolean val) { this.accepted = val; return this; }
    public Builder warnings(List<String> val) { this.warnings = val; return this; }

    public WriteChapterOutput build() {
      return new WriteChapterOutput(chapterContent, chapterTitle, chapterNumber, totalTokens,
        writeDurationMs, auditResult, revisionHistory, accepted, warnings);
    }
  }
}
