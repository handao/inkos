package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class OpenCodeCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("opencodeCodingPlan").label("OpenCode Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://opencode.ai/api/anthropic")
      .checkModel("glm-5.1")
      .temperatureRange(0, 1).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("glm-5.1").maxOutput(32000).contextWindowTokens(204800).releasedAt("2026-04-07").build(),
        builder().id("glm-5").maxOutput(32000).contextWindowTokens(204800).enabled(false).releasedAt("2026-02-11").build(),
        builder().id("kimi-k2.5").maxOutput(32000).contextWindowTokens(262144).enabled(false).releasedAt("2026-01-27").temperature(1.0).build(),
        builder().id("mimo-v2-omni").maxOutput(32000).contextWindowTokens(262144).enabled(false).releasedAt("2026-03-18").build(),
        builder().id("qwen3.6-plus").maxOutput(32000).contextWindowTokens(262144).releasedAt("2026-04-02").build(),
        builder().id("minimax-m2.5").maxOutput(32000).contextWindowTokens(204800).enabled(false).releasedAt("2026-02-12").build(),
        builder().id("minimax-m2.7").maxOutput(32000).contextWindowTokens(204800).releasedAt("2026-03-18").build(),
        builder().id("mimo-v2-pro").maxOutput(32000).contextWindowTokens(1048576).enabled(false).releasedAt("2026-03-18").build(),
        builder().id("qwen3.5-plus").maxOutput(32000).contextWindowTokens(262144).enabled(false).releasedAt("2026-02-16").build()
      )
      .build();
  }

  private OpenCodeCodingPlanEndpoints() {}
}
