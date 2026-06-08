package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class MistralEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("mistral").label("Mistral AI").group("overseas").api("openai-completions")
      .baseUrl("https://api.mistral.ai/v1")
      .checkModel("mistral-small-latest")
      .temperatureRange(0, 1).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("devstral-2512").maxOutput(4096).contextWindowTokens(262144).releasedAt("2025-12-09").build(),
        builder().id("labs-devstral-small-2512").maxOutput(4096).contextWindowTokens(262144).releasedAt("2025-12-09").build(),
        builder().id("mistral-medium-2508").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("magistral-medium-2509").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("magistral-small-2509").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("open-mistral-nemo").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("mistral-small-2603").maxOutput(4096).contextWindowTokens(256000).releasedAt("2026-03-16").build(),
        builder().id("mistral-small-2506").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("mistral-large-2512").maxOutput(4096).contextWindowTokens(256000).releasedAt("2025-12-02").build(),
        builder().id("mistral-large-2411").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("codestral-latest").maxOutput(4096).contextWindowTokens(256000).releasedAt("2025-07-30").build(),
        builder().id("pixtral-large-latest").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("pixtral-12b-2409").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("ministral-3b-latest").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("ministral-8b-latest").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("open-codestral-mamba").maxOutput(4096).contextWindowTokens(256000).build(),
        builder().id("labs-leanstral-2603").maxOutput(4096).contextWindowTokens(256000).releasedAt("2026-03-16").build()
      )
      .build();
  }

  private MistralEndpoints() {}
}
