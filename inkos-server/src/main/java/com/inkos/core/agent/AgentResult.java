package com.inkos.core.agent;

import java.util.List;
import java.util.Map;

public record AgentResult(
  String content,
  Map<String, Object> metadata,
  boolean success,
  String error
) {
  public record AuditIssue(
    String severity,
    String category,
    String description,
    String suggestion
  ) {}

  public static Builder builder() {
    return new Builder();
  }

  public static AgentResult ok(String content) {
    return new AgentResult(content, Map.of(), true, null);
  }

  public static AgentResult ok(String content, Map<String, Object> metadata) {
    return new AgentResult(content, metadata, true, null);
  }

  public static AgentResult failed(String error) {
    return new AgentResult(null, Map.of(), false, error);
  }

  public static class Builder {
    private String content;
    private Map<String, Object> metadata = Map.of();
    private boolean success = true;
    private String error;

    public Builder content(String content) { this.content = content; return this; }
    public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
    public Builder success(boolean success) { this.success = success; return this; }
    public Builder error(String error) { this.error = error; return this; }

    public AgentResult build() {
      return new AgentResult(content, metadata, success, error);
    }
  }
}
