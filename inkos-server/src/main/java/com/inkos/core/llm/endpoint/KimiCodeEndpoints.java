package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class KimiCodeEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("kimicode").label("Kimi Code").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://api.kimi.com/coding")
      .modelsBaseUrl("https://api.kimi.com/coding/v1")
      .checkModel("kimi-for-coding")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.0)
      .addModels(
        builder().id("kimi-for-coding").maxOutput(32768).contextWindowTokens(262144).build()
      )
      .build();
  }

  private KimiCodeEndpoints() {}
}
