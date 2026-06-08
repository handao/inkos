package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class Ai360Endpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("ai360").label("360 智脑").group("china").api("openai-completions")
      .baseUrl("https://api.360.cn/v1")
      .checkModel("360gpt2-pro")
      .temperatureRange(0, 2).defaultTemperature(0.5).writingTemperature(1.0)
      .addModels(
        builder().id("360zhinao3-o1.5").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("360zhinao2-o1.5").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("360zhinao2-o1").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("360zhinao-pro-32k-thinking-vision").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("360zhinao-turbo").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("360zhinao-turbo-qwen-plus").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("360gpt2-o1").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("360gpt2-pro").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("360gpt-pro").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("360gpt-pro-trans").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("360gpt-turbo").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("deepseek-v3.2").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("paratera/deepseek-v3.2").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("sophnet/deepseek-v3.2").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("deepseek-v3.2-speciale").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("360/deepseek-r1").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("volcengine/doubao-seed-2-0-lite").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("volcengine/doubao-seed-2-0-mini").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("volcengine/doubao-seed-2-0-pro").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("volcengine/doubao-seed-2-0-code").maxOutput(4096).contextWindowTokens(32000).build()
      )
      .build();
  }

  private Ai360Endpoints() {}
}
