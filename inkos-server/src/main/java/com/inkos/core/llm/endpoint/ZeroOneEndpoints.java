package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class ZeroOneEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("zeroone").label("零一万物 (01.AI)").group("china").api("openai-completions")
      .baseUrl("https://api.lingyiwanwu.com/v1")
      .checkModel("yi-lightning")
      .temperatureRange(0, 2).defaultTemperature(0.3).writingTemperature(1.0)
      .addModels(
        builder().id("yi-lightning").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-vision-v2").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-spark").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-medium").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-medium-200k").maxOutput(4096).contextWindowTokens(200000).build(),
        builder().id("yi-large-turbo").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-large-rag").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-large-fc").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("yi-large").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("yi-vision").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-large-preview").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("yi-lightning-lite").maxOutput(4096).contextWindowTokens(16384).build()
      )
      .build();
  }

  private ZeroOneEndpoints() {}
}
