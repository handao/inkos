package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class AstronCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("astronCodingPlan").label("讯飞星辰 Astron Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://maas-coding-api.cn-huabei-1.xf-yun.com/anthropic")
      .modelsBaseUrl("https://maas-coding-api.cn-huabei-1.xf-yun.com/v2")
      .checkModel("astron-code-latest")
      .temperatureRange(0, 1).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("astron-code-latest").maxOutput(32768).contextWindowTokens(131072).build()
      )
      .build();
  }

  private AstronCodingPlanEndpoints() {}
}
