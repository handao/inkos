package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class DeepSeekEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("deepseek").label("DeepSeek").group("china").api("openai-completions")
      .baseUrl("https://api.deepseek.com")
      .checkModel("deepseek-v4-flash")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.5)
      .temperatureHint("创意写作推荐 1.5")
      .compat(new EndpointConfig.ProviderCompat(null, null, null, true))
      .addModels(
        builder().id("deepseek-v4-flash").maxOutput(393216).contextWindowTokens(1_000_000).releasedAt("2026-04-24").build(),
        builder().id("deepseek-v4-pro").maxOutput(393216).contextWindowTokens(1_000_000).releasedAt("2026-04-24").build(),
        builder().id("deepseek-chat").maxOutput(393216).contextWindowTokens(1_000_000).releasedAt("2026-04-24").build(),
        builder().id("deepseek-reasoner").maxOutput(393216).contextWindowTokens(1_000_000).releasedAt("2026-04-24").build()
      )
      .build();
  }

  private DeepSeekEndpoints() {}
}
