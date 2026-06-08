package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class KimiCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("kimiCodingPlan").label("Kimi Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://api.moonshot.cn/anthropic")
      .checkModel("kimi-k2.5")
      .temperatureRange(0, 1).defaultTemperature(1.0).writingTemperature(1.0)
      .temperatureHint("kimi-k2.5 推荐 temperature=1.0")
      .addModels(
        builder().id("kimi-k2.5").maxOutput(32768).contextWindowTokens(262144).deploymentName("k2p5").releasedAt("2026-01-27").temperature(1.0).build(),
        builder().id("kimi-k2-thinking").maxOutput(65536).contextWindowTokens(262144).releasedAt("2025-11-06").temperature(1.0).build()
      )
      .build();
  }

  private KimiCodingPlanEndpoints() {}
}
