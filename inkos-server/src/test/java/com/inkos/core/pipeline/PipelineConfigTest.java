package com.inkos.core.pipeline;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PipelineConfigTest {

  @Test
  void builder_shouldUseDefaults() {
    var config = PipelineConfig.builder().build();

    assertEquals("v2", config.inputGovernanceMode());
    assertEquals(3, config.maxReviewCycles());
    assertEquals(16384, config.maxOutputTokens());
    assertTrue(config.enableAudit());
    assertTrue(config.enableRevision());
    assertTrue(config.enableRadar());
    assertTrue(config.enableContinuityCheck());
  }

  @Test
  void builder_shouldOverrideDefaults() {
    var config = PipelineConfig.builder()
      .inputGovernanceMode("legacy")
      .maxReviewCycles(5)
      .maxOutputTokens(32768)
      .enableAudit(false)
      .enableRevision(false)
      .enableRadar(false)
      .enableContinuityCheck(false)
      .build();

    assertEquals("legacy", config.inputGovernanceMode());
    assertEquals(5, config.maxReviewCycles());
    assertEquals(32768, config.maxOutputTokens());
    assertFalse(config.enableAudit());
    assertFalse(config.enableRevision());
  }

  @Test
  void governanceModeConstants_shouldBeCorrect() {
    assertEquals("v2", PipelineConfig.GOVERNANCE_MODE_V2);
    assertEquals("legacy", PipelineConfig.GOVERNANCE_MODE_LEGACY);
  }

  @Test
  void builder_shouldSupportPartialOverride() {
    var config = PipelineConfig.builder()
      .inputGovernanceMode("legacy")
      .enableAudit(false)
      .build();

    assertEquals("legacy", config.inputGovernanceMode());
    assertFalse(config.enableAudit());
    assertEquals(3, config.maxReviewCycles());
    assertEquals(16384, config.maxOutputTokens());
    assertTrue(config.enableRevision());
  }

  @Test
  void equality_shouldWork() {
    var a = PipelineConfig.builder().build();
    var b = PipelineConfig.builder().build();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
