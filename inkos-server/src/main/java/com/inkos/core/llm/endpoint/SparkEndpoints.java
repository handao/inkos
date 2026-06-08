package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class SparkEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("spark").label("讯飞星火").group("china").api("openai-completions")
      .baseUrl("https://spark-api-open.xf-yun.com/v1")
      .checkModel("lite")
      .temperatureRange(0, 1).defaultTemperature(0.5).writingTemperature(0.95)
      .addModels(
        builder().id("4.0Ultra").maxOutput(32768).contextWindowTokens(32768).build(),
        builder().id("pro-128k").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("max-32k").maxOutput(8192).contextWindowTokens(32768).build(),
        builder().id("generalv3.5").maxOutput(8192).contextWindowTokens(8192).build(),
        builder().id("generalv3").maxOutput(8192).contextWindowTokens(8192).build(),
        builder().id("lite").maxOutput(4096).contextWindowTokens(8192).build()
      )
      .build();
  }

  private SparkEndpoints() {}
}
