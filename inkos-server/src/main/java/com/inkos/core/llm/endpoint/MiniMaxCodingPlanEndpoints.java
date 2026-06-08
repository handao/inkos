package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class MiniMaxCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("minimaxCodingPlan").label("MiniMax Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://api.minimaxi.com/anthropic")
      .checkModel("MiniMax-M2.7")
      .transportDefaults(new EndpointConfig.TransportDefaults(null, false))
      .temperatureRange(0, 2).defaultTemperature(0.9).writingTemperature(0.9)
      .addModels(
        builder().id("MiniMax-M2.7").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-03-18").build(),
        builder().id("MiniMax-M2.7-highspeed").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-03-18").build(),
        builder().id("MiniMax-M2.5").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-02-12").build(),
        builder().id("MiniMax-M2.5-highspeed").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-02-12").build(),
        builder().id("MiniMax-M2.1").maxOutput(131072).contextWindowTokens(204800).releasedAt("2025-12-23").build(),
        builder().id("MiniMax-M2").maxOutput(131072).contextWindowTokens(204800).releasedAt("2025-12-23").build()
      )
      .build();
  }

  private MiniMaxCodingPlanEndpoints() {}
}
