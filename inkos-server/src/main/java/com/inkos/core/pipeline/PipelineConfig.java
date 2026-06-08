package com.inkos.core.pipeline;

/**
 * Configuration for the pipeline orchestrator.
 * Controls governance mode, max review cycles, length targets, and other pipeline-wide settings.
 */
public record PipelineConfig(
  String inputGovernanceMode,
  int maxReviewCycles,
  int maxOutputTokens,
  boolean enableAudit,
  boolean enableRevision,
  boolean enableRadar,
  boolean enableContinuityCheck
) {

  public static final String GOVERNANCE_MODE_V2 = "v2";
  public static final String GOVERNANCE_MODE_LEGACY = "legacy";

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String inputGovernanceMode = GOVERNANCE_MODE_V2;
    private int maxReviewCycles = 3;
    private int maxOutputTokens = 16384;
    private boolean enableAudit = true;
    private boolean enableRevision = true;
    private boolean enableRadar = true;
    private boolean enableContinuityCheck = true;

    public Builder inputGovernanceMode(String val) { this.inputGovernanceMode = val; return this; }
    public Builder maxReviewCycles(int val) { this.maxReviewCycles = val; return this; }
    public Builder maxOutputTokens(int val) { this.maxOutputTokens = val; return this; }
    public Builder enableAudit(boolean val) { this.enableAudit = val; return this; }
    public Builder enableRevision(boolean val) { this.enableRevision = val; return this; }
    public Builder enableRadar(boolean val) { this.enableRadar = val; return this; }
    public Builder enableContinuityCheck(boolean val) { this.enableContinuityCheck = val; return this; }

    public PipelineConfig build() {
      return new PipelineConfig(inputGovernanceMode, maxReviewCycles, maxOutputTokens,
        enableAudit, enableRevision, enableRadar, enableContinuityCheck);
    }
  }
}
