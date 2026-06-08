package com.inkos.core.pipeline;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WriteChapterOutputTest {

  @Test
  void builder_shouldUseDefaults() {
    var output = WriteChapterOutput.builder()
      .chapterContent("content")
      .chapterTitle("Chapter 1")
      .chapterNumber(1)
      .totalTokens(500)
      .writeDurationMs(1000)
      .build();

    assertEquals("content", output.chapterContent());
    assertEquals("Chapter 1", output.chapterTitle());
    assertEquals(1, output.chapterNumber());
    assertTrue(output.accepted());
    assertTrue(output.warnings().isEmpty());
    assertTrue(output.revisionHistory().isEmpty());
    assertNull(output.auditResult());
  }

  @Test
  void builder_shouldBuildWithAllFields() {
    var auditResult = new WriteChapterOutput.AuditResult(true, 0, List.of(), 95.0);
    var revision = new WriteChapterOutput.RevisionAttempt(1, "revised content", List.of("fixed typo"), 500);
    var output = WriteChapterOutput.builder()
      .chapterContent("content")
      .chapterTitle("Chapter 1")
      .chapterNumber(1)
      .totalTokens(500)
      .writeDurationMs(1000)
      .auditResult(auditResult)
      .revisionHistory(List.of(revision))
      .accepted(false)
      .warnings(List.of("low score"))
      .build();

    assertEquals("content", output.chapterContent());
    assertFalse(output.accepted());
    assertEquals(1, output.revisionHistory().size());
    assertTrue(output.warnings().contains("low score"));
    assertTrue(output.auditResult().passed());
  }

  @Test
  void auditResult_shouldCreateRecord() {
    var result = new WriteChapterOutput.AuditResult(false, 3,
      List.of("OOC issue", "timeline error", "info leak"), 70.5);

    assertFalse(result.passed());
    assertEquals(3, result.issueCount());
    assertEquals(3, result.issues().size());
    assertEquals(70.5, result.continuityScore());
  }

  @Test
  void revisionAttempt_shouldCreateRecord() {
    var attempt = new WriteChapterOutput.RevisionAttempt(1, "revised", List.of("change 1", "change 2"), 1500);

    assertEquals(1, attempt.attemptNumber());
    assertEquals("revised", attempt.revisedContent());
    assertEquals(2, attempt.changes().size());
    assertEquals(1500, attempt.durationMs());
  }

  @Test
  void builder_shouldSupportChain() {
    var output = WriteChapterOutput.builder()
      .chapterContent("hello")
      .chapterTitle("Title")
      .chapterNumber(5)
      .totalTokens(1000)
      .writeDurationMs(2000)
      .accepted(true)
      .build();

    assertEquals("hello", output.chapterContent());
    assertEquals("Title", output.chapterTitle());
    assertEquals(5, output.chapterNumber());
  }

  @Test
  void equality_shouldWork() {
    var a = WriteChapterOutput.builder()
      .chapterContent("c").chapterTitle("t").chapterNumber(1)
      .totalTokens(100).writeDurationMs(50).build();
    var b = WriteChapterOutput.builder()
      .chapterContent("c").chapterTitle("t").chapterNumber(1)
      .totalTokens(100).writeDurationMs(50).build();
    assertEquals(a, b);
  }
}
