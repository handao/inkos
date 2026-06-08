package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class TencentCloudEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("tencentcloud").label("腾讯云 (lkeap)").group("china").api("openai-completions")
      .baseUrl("https://api.lkeap.cloud.tencent.com/v1")
      .checkModel("deepseek-v3")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("deepseek-r1").maxOutput(16000).contextWindowTokens(65536).build(),
        builder().id("deepseek-v3-0324").maxOutput(16000).contextWindowTokens(65536).build(),
        builder().id("deepseek-v3").maxOutput(16000).contextWindowTokens(65536).build()
      )
      .build();
  }

  private TencentCloudEndpoints() {}
}
