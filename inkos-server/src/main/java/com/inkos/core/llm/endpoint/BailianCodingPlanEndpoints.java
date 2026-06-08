package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class BailianCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("bailianCodingPlan").label("百炼 Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://dashscope.aliyuncs.com/apps/anthropic")
      .checkModel("qwen-max")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("qwen3.5-plus").maxOutput(65536).contextWindowTokens(1000000).releasedAt("2026-02-15").build(),
        builder().id("qwen3-coder-plus").maxOutput(65536).contextWindowTokens(1000000).releasedAt("2025-09-23").build(),
        builder().id("qwen3-max-2026-01-23").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-01-23").build(),
        builder().id("qwen3-coder-next").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-02-15").build(),
        builder().id("glm-5").maxOutput(131072).contextWindowTokens(200000).releasedAt("2026-02-12").build(),
        builder().id("glm-4.7").maxOutput(131072).contextWindowTokens(200000).releasedAt("2025-12-01").build(),
        builder().id("kimi-k2.5").maxOutput(32768).contextWindowTokens(262144).releasedAt("2026-01-27").temperature(1.0).build(),
        builder().id("MiniMax-M2.5").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-02-12").build()
      )
      .build();
  }

  private BailianCodingPlanEndpoints() {}
}
