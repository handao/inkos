package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class MiniMaxEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("minimax").label("MiniMax").group("china").api("openai-completions")
      .baseUrl("https://api.minimaxi.com/v1")
      .checkModel("MiniMax-M2.7")
      .transportDefaults(new EndpointConfig.TransportDefaults(null, false))
      .temperatureRange(0, 1).defaultTemperature(0.9).writingTemperature(0.9)
      .addModels(
        builder().id("MiniMax-M2.7").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-03-18").build(),
        builder().id("MiniMax-M2.7-highspeed").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-03-18").build(),
        builder().id("MiniMax-M2.5").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-02-12").build(),
        builder().id("MiniMax-M2.5-highspeed").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-02-12").build(),
        builder().id("M2-her").maxOutput(2048).contextWindowTokens(65536).releasedAt("2026-01-23").build(),
        builder().id("MiniMax-M2.1").maxOutput(131072).contextWindowTokens(204800).releasedAt("2025-12-23").build(),
        builder().id("MiniMax-M2.1-highspeed").maxOutput(131072).contextWindowTokens(204800).releasedAt("2025-12-23").build(),
        builder().id("MiniMax-M2").maxOutput(131072).contextWindowTokens(204800).releasedAt("2025-10-27").build(),
        builder().id("MiniMax-M2-Stable").maxOutput(131072).contextWindowTokens(204800).releasedAt("2025-10-27").build(),
        builder().id("MiniMax-M1").maxOutput(40000).contextWindowTokens(1000192).releasedAt("2025-06-16").build(),
        builder().id("MiniMax-Text-01").maxOutput(40000).contextWindowTokens(1000192).releasedAt("2025-01-15").build()
      )
      .build();
  }

  private MiniMaxEndpoints() {}
}
