package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class GiteeAiEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("giteeai").label("Gitee AI").group("aggregator").api("openai-completions")
      .baseUrl("https://ai.gitee.com/v1")
      .checkModel("Qwen2.5-72B-Instruct")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("DeepSeek-R1-Distill-Qwen-1.5B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("DeepSeek-R1-Distill-Qwen-7B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("DeepSeek-R1-Distill-Qwen-14B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("DeepSeek-R1-Distill-Qwen-32B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("QwQ-32B-Preview").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("Qwen2.5-72B-Instruct").maxOutput(4096).contextWindowTokens(16000).build(),
        builder().id("Qwen2.5-32B-Instruct").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("Qwen2.5-14B-Instruct").maxOutput(4096).contextWindowTokens(24000).build(),
        builder().id("Qwen2.5-7B-Instruct").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("Qwen2-72B-Instruct").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("Qwen2-7B-Instruct").maxOutput(4096).contextWindowTokens(24000).build(),
        builder().id("Qwen2.5-Coder-32B-Instruct").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("Qwen2.5-Coder-14B-Instruct").maxOutput(4096).contextWindowTokens(24000).build(),
        builder().id("Qwen2-VL-72B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("InternVL2.5-26B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("InternVL2-8B").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("glm-4-9b-chat").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("deepseek-coder-33B-instruct").maxOutput(4096).contextWindowTokens(8000).build(),
        builder().id("codegeex4-all-9b").maxOutput(4096).contextWindowTokens(32000).build()
      )
      .build();
  }

  private GiteeAiEndpoints() {}
}
