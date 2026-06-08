package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class SenseNovaEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("sensenova").label("商汤日日新").group("china").api("openai-completions")
      .baseUrl("https://api.sensenova.cn/compatible-mode/v1")
      .checkModel("SenseChat-Turbo")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("SenseNova-V6-5-Pro").maxOutput(4096).contextWindowTokens(131072).releasedAt("2025-07-23").build(),
        builder().id("SenseNova-V6-5-Turbo").maxOutput(4096).contextWindowTokens(131072).releasedAt("2025-07-23").build(),
        builder().id("Qwen3-235B").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-05-27").build(),
        builder().id("Qwen3-32B").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-05-27").build(),
        builder().id("SenseNova-V6-Reasoner").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-04-14").build(),
        builder().id("SenseNova-V6-Turbo").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-04-14").build(),
        builder().id("SenseNova-V6-Pro").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-04-14").build(),
        builder().id("SenseChat-5-beta").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("SenseChat-5-1202").maxOutput(4096).contextWindowTokens(32768).releasedAt("2024-12-30").build(),
        builder().id("SenseChat-Turbo-1202").maxOutput(4096).contextWindowTokens(32768).releasedAt("2024-12-30").build(),
        builder().id("SenseChat-5").maxOutput(131072).contextWindowTokens(131072).build(),
        builder().id("SenseChat-Vision").maxOutput(16384).contextWindowTokens(16384).releasedAt("2024-09-12").build(),
        builder().id("SenseChat-Turbo").maxOutput(32768).contextWindowTokens(32768).build(),
        builder().id("SenseChat-128K").maxOutput(131072).contextWindowTokens(131072).build(),
        builder().id("SenseChat-32K").maxOutput(32768).contextWindowTokens(32768).build(),
        builder().id("SenseChat").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("SenseChat-5-Cantonese").maxOutput(32768).contextWindowTokens(32768).build(),
        builder().id("SenseChat-Character").maxOutput(1024).contextWindowTokens(8192).build(),
        builder().id("SenseChat-Character-Pro").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("DeepSeek-V3").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("DeepSeek-R1").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("DeepSeek-R1-Distill-Qwen-14B").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("DeepSeek-R1-Distill-Qwen-32B").maxOutput(4096).contextWindowTokens(8192).build()
      )
      .build();
  }

  private SenseNovaEndpoints() {}
}
