package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import com.inkos.core.llm.EndpointConfig.ModelCard.Capabilities;
import java.util.List;

public final class KkAiApiEndpoints {

  public static EndpointConfig getEndpoint() {
    var nonText = Capabilities.builder().text(false).imageOutput(true).build();
    return EndpointConfig.builder()
      .id("kkaiapi").label("kkaiapi").group("aggregator").api("openai-completions")
      .baseUrl("https://api.kkaiapi.com/v1")
      .modelsBaseUrl("https://api.kkaiapi.com/v1")
      .checkModel("deepseek-v4-flash")
      .temperatureRange(0, 2).defaultTemperature(0.9).writingTemperature(1.2)
      .addModels(
        builder().id("deepseek-v4-flash").maxOutput(393216).contextWindowTokens(1_000_000).releasedAt("2026-04-24").build(),
        builder().id("deepseek-v4-pro").maxOutput(393216).contextWindowTokens(1_000_000).releasedAt("2026-04-24").build(),
        builder().id("gpt-5.5").maxOutput(128000).contextWindowTokens(1_050_000).build(),
        builder().id("gpt-5.4").maxOutput(128000).contextWindowTokens(1_050_000).build(),
        builder().id("gpt-5.4-mini").maxOutput(128000).contextWindowTokens(400000).build(),
        builder().id("gpt-5.4-nano").maxOutput(128000).contextWindowTokens(400000).build(),
        builder().id("gpt-5.3-codex").maxOutput(128000).contextWindowTokens(400000).build(),
        builder().id("gpt-5.3-codex-spark").maxOutput(128000).contextWindowTokens(400000).build(),
        builder().id("gpt-5.2").maxOutput(128000).contextWindowTokens(400000).build(),
        builder().id("claude-opus-4-7").maxOutput(128000).contextWindowTokens(1_000_000).build(),
        builder().id("claude-opus-4-6").maxOutput(128000).contextWindowTokens(1_000_000).build(),
        builder().id("claude-sonnet-4-6").maxOutput(64000).contextWindowTokens(1_000_000).build(),
        builder().id("claude-sonnet-4-5-20250929").maxOutput(64000).contextWindowTokens(200000).build(),
        builder().id("claude-haiku-4-5").maxOutput(64000).contextWindowTokens(200000).build(),
        builder().id("claude-haiku-4-5-20251001").maxOutput(64000).contextWindowTokens(200000).build(),
        builder().id("gemini-3.1-pro-preview").maxOutput(65536).contextWindowTokens(1_048_576).build(),
        builder().id("glm-5.1").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("glm-5").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("kimi-k2.6").maxOutput(32768).contextWindowTokens(256000).temperature(1.0).build(),
        builder().id("kimi-k2.5").maxOutput(32768).contextWindowTokens(256000).temperature(1.0).build(),
        builder().id("qwen3.6-plus").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("qwen3.5-plus").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("mimo-v2.5-pro").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("mimo-v2.5").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("mimo-v2-pro").maxOutput(32768).contextWindowTokens(128000).build(),
        builder().id("MiniMax-M2.7").maxOutput(64000).contextWindowTokens(1_000_000).enabled(false).status("disabled").build(),
        builder().id("gpt-image-2-pro").maxOutput(1).contextWindowTokens(1).enabled(false).status("nonText").capabilities(nonText).build(),
        builder().id("gpt-image-2").maxOutput(1).contextWindowTokens(1).enabled(false).status("nonText").capabilities(nonText).build()
      )
      .build();
  }

  private KkAiApiEndpoints() {}
}
