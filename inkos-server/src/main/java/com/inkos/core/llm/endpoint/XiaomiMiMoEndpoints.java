package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class XiaomiMiMoEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("xiaomimimo").label("小米 MiMo").group("china").api("openai-completions")
      .baseUrl("https://api-ai.xiaomi.com/v1")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("mimo-v2-pro").maxOutput(131072).contextWindowTokens(1000000).releasedAt("2026-03-18").build(),
        builder().id("mimo-v2-omni").maxOutput(131072).contextWindowTokens(262144).releasedAt("2026-03-18").build(),
        builder().id("mimo-v2-flash").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-03-03").build()
      )
      .build();
  }

  private XiaomiMiMoEndpoints() {}
}
