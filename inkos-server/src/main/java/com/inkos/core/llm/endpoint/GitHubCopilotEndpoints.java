package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class GitHubCopilotEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("githubCopilot").label("GitHub Copilot").group("local").api("openai-responses")
      .baseUrl("https://api.githubcopilot.com")
      .checkModel("gpt-4o")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.0)
      .addModels(
        builder().id("gpt-5.4").maxOutput(4096).contextWindowTokens(400000).releasedAt("2026-03-05").build(),
        builder().id("gpt-5.4-mini").maxOutput(4096).contextWindowTokens(400000).releasedAt("2026-03-18").build(),
        builder().id("gpt-5.3-codex").maxOutput(4096).contextWindowTokens(400000).releasedAt("2026-02-05").build(),
        builder().id("gpt-5.2").maxOutput(4096).contextWindowTokens(192000).releasedAt("2025-12-11").build(),
        builder().id("gpt-5.2-codex").maxOutput(4096).contextWindowTokens(400000).releasedAt("2025-12-18").build(),
        builder().id("gpt-5.1").maxOutput(4096).contextWindowTokens(192000).releasedAt("2025-11-13").build(),
        builder().id("gpt-5-mini").maxOutput(4096).contextWindowTokens(192000).releasedAt("2025-08-07").build(),
        builder().id("gpt-4.1").maxOutput(4096).contextWindowTokens(128000).releasedAt("2025-04-14").build(),
        builder().id("claude-opus-4.6").maxOutput(4096).contextWindowTokens(139000).releasedAt("2026-02-05").build(),
        builder().id("claude-opus-4.6-fast").maxOutput(4096).contextWindowTokens(139000).releasedAt("2026-02-05").build(),
        builder().id("claude-sonnet-4.6").maxOutput(4096).contextWindowTokens(139000).releasedAt("2026-02-17").build(),
        builder().id("claude-opus-4.5").maxOutput(4096).contextWindowTokens(139000).releasedAt("2025-11-24").build(),
        builder().id("claude-sonnet-4.5").maxOutput(4096).contextWindowTokens(139000).releasedAt("2025-09-29").build(),
        builder().id("claude-haiku-4.5").maxOutput(4096).contextWindowTokens(139000).releasedAt("2025-10-16").build(),
        builder().id("claude-sonnet-4").maxOutput(4096).contextWindowTokens(139000).releasedAt("2025-05-23").build(),
        builder().id("gemini-3.1-pro-preview").maxOutput(4096).contextWindowTokens(173000).releasedAt("2026-02-19").build(),
        builder().id("gemini-3-flash-preview").maxOutput(4096).contextWindowTokens(173000).releasedAt("2025-12-17").build(),
        builder().id("gemini-2.5-pro").maxOutput(4096).contextWindowTokens(173000).releasedAt("2025-06-17").build(),
        builder().id("grok-code-fast-1").maxOutput(4096).contextWindowTokens(173000).releasedAt("2025-08-27").build(),
        builder().id("oswe-vscode-prime").maxOutput(4096).contextWindowTokens(264000).build(),
        builder().id("oswe-vscode-secondary").maxOutput(4096).contextWindowTokens(264000).build()
      )
      .build();
  }

  private GitHubCopilotEndpoints() {}
}
