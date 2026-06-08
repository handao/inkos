package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class XaiEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("xai").label("xAI (Grok)").group("overseas").api("openai-completions")
      .baseUrl("https://api.x.ai/v1")
      .checkModel("grok-2-1212")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.0)
      .addModels(
        builder().id("grok-4.20-beta-0309-reasoning").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2026-03-09").build(),
        builder().id("grok-4.20-beta-0309-non-reasoning").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2026-03-09").build(),
        builder().id("grok-4.20-multi-agent-beta-0309").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2026-03-09").build(),
        builder().id("grok-4-1-fast-non-reasoning").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2025-11-20").build(),
        builder().id("grok-4-1-fast-reasoning").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2025-11-20").build(),
        builder().id("grok-4-fast-non-reasoning").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2025-09-09").build(),
        builder().id("grok-4-fast-reasoning").maxOutput(4096).contextWindowTokens(2000000).releasedAt("2025-09-09").build(),
        builder().id("grok-code-fast-1").maxOutput(4096).contextWindowTokens(256000).releasedAt("2025-08-27").build(),
        builder().id("grok-4").maxOutput(4096).contextWindowTokens(256000).releasedAt("2025-07-09").build(),
        builder().id("grok-3").maxOutput(4096).contextWindowTokens(131072).releasedAt("2025-04-03").build(),
        builder().id("grok-3-mini").maxOutput(4096).contextWindowTokens(131072).releasedAt("2025-04-03").build()
      )
      .build();
  }

  private XaiEndpoints() {}
}
