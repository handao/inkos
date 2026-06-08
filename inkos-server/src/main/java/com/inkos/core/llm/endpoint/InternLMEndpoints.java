package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class InternLMEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("internlm").label("书生浦语 (InternLM)").group("china").api("openai-completions")
      .baseUrl("https://chat.intern-ai.org.cn/api/v1")
      .checkModel("internlm2.5-latest")
      .temperatureRange(0, 2).defaultTemperature(0.8).writingTemperature(1.0)
      .addModels(
        builder().id("intern-latest").maxOutput(4096).contextWindowTokens(262144).releasedAt("2026-02-04").build(),
        builder().id("intern-s1-pro").maxOutput(4096).contextWindowTokens(262144).releasedAt("2026-02-04").build(),
        builder().id("intern-s1").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-07-26").build(),
        builder().id("intern-s1-mini").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-08-20").build(),
        builder().id("internvl3.5-latest").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-08-28").build(),
        builder().id("internvl3.5-241b-a28b").maxOutput(4096).contextWindowTokens(32768).releasedAt("2025-08-28").build()
      )
      .build();
  }

  private InternLMEndpoints() {}
}
