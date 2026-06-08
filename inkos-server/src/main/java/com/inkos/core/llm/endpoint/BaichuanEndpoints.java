package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class BaichuanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("baichuan").label("百川智能").group("china").api("openai-completions")
      .baseUrl("https://api.baichuan-ai.com/v1")
      .checkModel("Baichuan4")
      .temperatureRange(0, 1).defaultTemperature(0.3).writingTemperature(1.0)
      .addModels(
        builder().id("Baichuan4").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("Baichuan4-Turbo").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("Baichuan4-Air").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("Baichuan3-Turbo").maxOutput(8192).contextWindowTokens(32768).build(),
        builder().id("Baichuan3-Turbo-128k").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("Baichuan2-Turbo").maxOutput(8192).contextWindowTokens(32768).build()
      )
      .build();
  }

  private BaichuanEndpoints() {}
}
