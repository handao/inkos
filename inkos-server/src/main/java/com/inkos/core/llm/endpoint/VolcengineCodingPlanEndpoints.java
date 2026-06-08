package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class VolcengineCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("volcengineCodingPlan").label("火山 Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://ark.cn-beijing.volces.com/api/coding")
      .checkModel("doubao-seed-2.0-code")
      .temperatureRange(0, 1).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("doubao-seed-2.0-code").maxOutput(128000).contextWindowTokens(256000).releasedAt("2026-02-15").build(),
        builder().id("doubao-seed-2.0-pro").maxOutput(128000).contextWindowTokens(256000).releasedAt("2026-02-15").build(),
        builder().id("doubao-seed-2.0-lite").maxOutput(128000).contextWindowTokens(256000).releasedAt("2026-02-15").build(),
        builder().id("doubao-seed-code").maxOutput(32000).contextWindowTokens(256000).releasedAt("2025-11-01").build(),
        builder().id("minimax-m2.5").maxOutput(131072).contextWindowTokens(204800).build(),
        builder().id("glm-4.7").maxOutput(131072).contextWindowTokens(200000).build(),
        builder().id("deepseek-v3.2").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("kimi-k2.5").maxOutput(32768).contextWindowTokens(262144).temperature(1.0).build()
      )
      .build();
  }

  private VolcengineCodingPlanEndpoints() {}
}
