package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class ModelScopeEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("modelscope").label("魔搭社区 ModelScope").group("aggregator").api("openai-completions")
      .baseUrl("https://api-inference.modelscope.cn/v1")
      .checkModel("Qwen/Qwen2.5-72B-Instruct")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("Qwen/Qwen3-Next-80B-A3B-Thinking").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("Qwen/Qwen3-Next-80B-A3B-Instruct").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("deepseek-ai/DeepSeek-V3.2").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("deepseek-ai/DeepSeek-V3.2-Exp").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("deepseek-ai/DeepSeek-V3.1").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("deepseek-ai/DeepSeek-R1-0528").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("Qwen/Qwen3-235B-A22B").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("Qwen/Qwen3-32B").maxOutput(4096).contextWindowTokens(131072).build()
      )
      .build();
  }

  private ModelScopeEndpoints() {}
}
