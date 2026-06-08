package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class AnthropicEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("anthropic").label("Anthropic").group("overseas").api("anthropic-messages")
      .baseUrl("https://api.anthropic.com")
      .checkModel("claude-haiku-4-5-20251001")
      .temperatureRange(0, 1).defaultTemperature(1.0).writingTemperature(1.0)
      .temperatureHint("不要同时改 temperature 和 top_p")
      .addModels(
        builder().id("claude-opus-4-6").maxOutput(128000).contextWindowTokens(1_000_000).releasedAt("2026-02-05").build(),
        builder().id("claude-sonnet-4-6").maxOutput(64000).contextWindowTokens(1_000_000).releasedAt("2026-02-17").build(),
        builder().id("claude-opus-4-5-20251101").maxOutput(64000).contextWindowTokens(200_000).releasedAt("2025-11-24").build(),
        builder().id("claude-sonnet-4-5-20250929").maxOutput(64000).contextWindowTokens(200_000).releasedAt("2025-09-29").build(),
        builder().id("claude-haiku-4-5-20251001").maxOutput(64000).contextWindowTokens(200_000).releasedAt("2025-10-16").build(),
        builder().id("claude-opus-4-1-20250805").maxOutput(32000).contextWindowTokens(200_000).releasedAt("2025-08-05").build(),
        builder().id("claude-opus-4-20250514").maxOutput(32000).contextWindowTokens(200_000).releasedAt("2025-05-23").build(),
        builder().id("claude-sonnet-4-20250514").maxOutput(64000).contextWindowTokens(200_000).releasedAt("2025-05-23").build(),
        builder().id("claude-3-haiku-20240307").maxOutput(4096).contextWindowTokens(200_000).releasedAt("2024-03-07").build()
      )
      .build();
  }

  private AnthropicEndpoints() {}
}
