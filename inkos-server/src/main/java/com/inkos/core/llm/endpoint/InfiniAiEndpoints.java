package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class InfiniAiEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("infiniai").label("无问芯穹 InfiniAI").group("aggregator").api("openai-completions")
      .baseUrl("https://cloud.infini-ai.com/maas/v1")
      .checkModel("qwen2.5-72b-instruct")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("minimax-m2.7").maxOutput(4096).contextWindowTokens(204800).releasedAt("2026-03-17").build(),
        builder().id("deepseek-v3.2").maxOutput(8192).contextWindowTokens(131072).releasedAt("2025-12-02").build(),
        builder().id("deepseek-v3.2-thinking").maxOutput(65536).contextWindowTokens(131072).releasedAt("2025-12-02").build(),
        builder().id("glm-4.6v").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("glm-5").maxOutput(4096).contextWindowTokens(198000).releasedAt("2026-02-13").build(),
        builder().id("minimax-m2.5").maxOutput(4096).contextWindowTokens(204800).releasedAt("2026-02-13").build(),
        builder().id("kimi-k2-thinking").maxOutput(32768).contextWindowTokens(262144).releasedAt("2025-11-07").temperature(1.0).build(),
        builder().id("deepseek-ocr").maxOutput(8192).contextWindowTokens(8192).releasedAt("2025-10-20").build(),
        builder().id("minimax-m2.1").maxOutput(200000).contextWindowTokens(200000).build(),
        builder().id("minimax-m2").maxOutput(200000).contextWindowTokens(200000).build(),
        builder().id("glm-4.7").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("glm-4.6").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("deepseek-v3.2-exp").maxOutput(65536).contextWindowTokens(131072).build(),
        builder().id("qwen3-vl-235b-a22b-instruct").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("qwen3-vl-235b-a22b-thinking").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("deepseek-v3.1-terminus").maxOutput(65536).contextWindowTokens(131072).build(),
        builder().id("qwen3-next-80b-a3b-thinking").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("qwen3-next-80b-a3b-instruct").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("deepseek-v3.1").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("baichuan-m2-32b").maxOutput(8192).contextWindowTokens(64000).build(),
        builder().id("glm-4.5v").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("glm-4.5").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("glm-4.5-air").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("qwen3-coder-480b-a35b-instruct").maxOutput(32768).contextWindowTokens(262144).build(),
        builder().id("qwen3-235b-a22b-instruct-2507").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("kimi-k2-instruct").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("ernie-4.5-300b-a47b").maxOutput(8192).contextWindowTokens(32000).build(),
        builder().id("ernie-4.5-21b-a3b").maxOutput(8192).contextWindowTokens(120000).build(),
        builder().id("qwen3-8b").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen3-14b").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen3-32b").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen3-30b-a3b").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen3-235b-a22b").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen2.5-vl-72b-instruct").maxOutput(4096).contextWindowTokens(125000).build(),
        builder().id("qwen2.5-vl-32b-instruct").maxOutput(4096).contextWindowTokens(125000).build(),
        builder().id("qwen2.5-vl-7b-instruct").maxOutput(4096).contextWindowTokens(125000).build(),
        builder().id("qwq-32b").maxOutput(8192).contextWindowTokens(32000).build(),
        builder().id("deepseek-v3").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("deepseek-r1").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("deepseek-r1-distill-qwen-32b").maxOutput(8192).contextWindowTokens(32000).build(),
        builder().id("megrez-3b-instruct").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("qwen2.5-32b-instruct").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("qwen2.5-72b-instruct").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("qwen2.5-14b-instruct").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("qwen2.5-7b-instruct").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("qwen2.5-coder-32b-instruct").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("gpt-oss-120b").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("gpt-oss-20b").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("pro-deepseek-r1").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("pro-deepseek-v3").maxOutput(16384).contextWindowTokens(131072).build()
      )
      .build();
  }

  private InfiniAiEndpoints() {}
}
