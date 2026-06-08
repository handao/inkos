package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class LongCatEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("longcat").label("美团 LongCat").group("china").api("openai-completions")
      .baseUrl("https://api.longcat.chat/openai/v1")
      .checkModel("LongCat-Flash-Chat")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("LongCat-Flash-Lite").maxOutput(4096).contextWindowTokens(327680).releasedAt("2026-02-05").build(),
        builder().id("LongCat-Flash-Thinking-2601").maxOutput(4096).contextWindowTokens(262144).releasedAt("2026-01-14").build(),
        builder().id("LongCat-Flash-Thinking").maxOutput(4096).contextWindowTokens(262144).releasedAt("2025-09-22").build(),
        builder().id("LongCat-Flash-Chat").maxOutput(4096).contextWindowTokens(262144).releasedAt("2025-12-12").build()
      )
      .build();
  }

  private LongCatEndpoints() {}
}
