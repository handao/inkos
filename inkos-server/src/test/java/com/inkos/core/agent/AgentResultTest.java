package com.inkos.core.agent;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AgentResultTest {

  @Test
  void ok_shouldCreateSuccessfulResult() {
    var result = AgentResult.ok("chapter content");
    assertTrue(result.success());
    assertEquals("chapter content", result.content());
    assertNull(result.error());
    assertTrue(result.metadata().isEmpty());
  }

  @Test
  void ok_shouldCreateWithMetadata() {
    var result = AgentResult.ok("content", Map.of("key", "value"));
    assertTrue(result.success());
    assertEquals("value", result.metadata().get("key"));
  }

  @Test
  void failed_shouldCreateFailedResult() {
    var result = AgentResult.failed("something went wrong");
    assertFalse(result.success());
    assertEquals("something went wrong", result.error());
    assertNull(result.content());
  }

  @Test
  void builder_shouldBuildWithAllFields() {
    var result = AgentResult.builder()
      .content("hello")
      .metadata(Map.of("tokens", 100))
      .success(true)
      .error(null)
      .build();

    assertEquals("hello", result.content());
    assertEquals(100, result.metadata().get("tokens"));
    assertTrue(result.success());
  }

  @Test
  void builder_shouldBuildFailedResult() {
    var result = AgentResult.builder()
      .content(null)
      .success(false)
      .error("error message")
      .build();

    assertFalse(result.success());
    assertEquals("error message", result.error());
  }

  @Test
  void auditIssue_shouldCreateRecord() {
    var issue = new AgentResult.AuditIssue("critical", "ooc", "Character acted out of character", "Revise dialogue");
    assertEquals("critical", issue.severity());
    assertEquals("ooc", issue.category());
    assertEquals("Character acted out of character", issue.description());
    assertEquals("Revise dialogue", issue.suggestion());
  }

  @Test
  void auditIssue_shouldSupportEquality() {
    var a = new AgentResult.AuditIssue("warning", "timeline", "Time inconsistency", "Fix date");
    var b = new AgentResult.AuditIssue("warning", "timeline", "Time inconsistency", "Fix date");
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void equality_shouldWorkForAgentResults() {
    var a = AgentResult.ok("content");
    var b = AgentResult.ok("content");
    assertEquals(a, b);
  }

  @Test
  void builder_shouldDefaultSuccessToTrue() {
    var r = AgentResult.builder().content("c").build();
    assertTrue(r.success());
  }

  @Test
  void auditIssue_shouldHandleEmptyStrings() {
    var issue = new AgentResult.AuditIssue("", "", "", "");
    assertAll(
      () -> assertEquals("", issue.severity()),
      () -> assertEquals("", issue.category()),
      () -> assertEquals("", issue.description()),
      () -> assertEquals("", issue.suggestion())
    );
  }

  @Test
  void auditIssue_shouldHandleSeverityVariants() {
    var critical = new AgentResult.AuditIssue("critical", "ooc", "desc", "sugg");
    var warning = new AgentResult.AuditIssue("warning", "timeline", "desc", "sugg");
    var info = new AgentResult.AuditIssue("info", "style", "desc", "sugg");

    assertAll(
      () -> assertEquals("critical", critical.severity()),
      () -> assertEquals("warning", warning.severity()),
      () -> assertEquals("info", info.severity())
    );
  }

  @Test
  void failed_shouldHandleEmptyErrorMessage() {
    var r = AgentResult.failed("");
    assertAll(
      () -> assertFalse(r.success()),
      () -> assertNull(r.content()),
      () -> assertEquals("", r.error())
    );
  }

  @Test
  void ok_shouldSupportEmptyContent() {
    var r = AgentResult.ok("");
    assertTrue(r.success());
    assertEquals("", r.content());
  }

  @Test
  void builder_shouldSupportPartialBuild() {
    var r = AgentResult.builder().content("partial").build();
    assertAll(
      () -> assertEquals("partial", r.content()),
      () -> assertTrue(r.metadata().isEmpty()),
      () -> assertTrue(r.success()),
      () -> assertNull(r.error())
    );
  }

  @Test
  void result_shouldHandleNullMetadata() {
    var r = new AgentResult("c", null, true, null);
    assertNull(r.metadata());
  }
}
