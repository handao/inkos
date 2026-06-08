package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class OpenAiEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("openai").label("OpenAI").group("overseas").api("openai-responses")
      .baseUrl("https://api.openai.com/v1")
      .checkModel("gpt-4o-mini")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.0)
      .addModels(
        builder().id("gpt-5.4").maxOutput(128000).contextWindowTokens(1050000).releasedAt("2026-03-05").build(),
        builder().id("gpt-5.4-pro").maxOutput(128000).contextWindowTokens(1050000).releasedAt("2026-03-05").build(),
        builder().id("gpt-5.4-mini").maxOutput(128000).contextWindowTokens(400000).releasedAt("2026-03-18").build(),
        builder().id("gpt-5.4-nano").maxOutput(128000).contextWindowTokens(400000).releasedAt("2026-03-18").build(),
        builder().id("gpt-5.3-chat-latest").maxOutput(16384).contextWindowTokens(128000).releasedAt("2026-03-04").build(),
        builder().id("gpt-5.3-codex").maxOutput(128000).contextWindowTokens(400000).releasedAt("2026-02-05").build(),
        builder().id("gpt-5.2").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-12-11").build(),
        builder().id("gpt-5.2-codex").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-12-18").build(),
        builder().id("gpt-5.2-pro").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-12-11").build(),
        builder().id("gpt-5.2-chat-latest").maxOutput(16384).contextWindowTokens(128000).releasedAt("2025-12-11").build(),
        builder().id("gpt-5.1").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-11-13").build(),
        builder().id("gpt-5.1-chat-latest").maxOutput(16384).contextWindowTokens(128000).releasedAt("2025-11-13").build(),
        builder().id("gpt-5.1-codex-max").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-12-04").build(),
        builder().id("gpt-5.1-codex").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-11-13").build(),
        builder().id("gpt-5.1-codex-mini").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-11-13").build(),
        builder().id("gpt-5-pro").maxOutput(272000).contextWindowTokens(400000).releasedAt("2025-10-06").build(),
        builder().id("gpt-5-codex").maxOutput(128000).contextWindowTokens(400000).releasedAt("2024-09-15").build(),
        builder().id("gpt-5").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-08-07").build(),
        builder().id("gpt-5-mini").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-08-07").build(),
        builder().id("gpt-5-nano").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-08-07").build(),
        builder().id("gpt-5-chat-latest").maxOutput(128000).contextWindowTokens(400000).releasedAt("2025-08-07").build(),
        builder().id("o4-mini").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-04-17").build(),
        builder().id("o4-mini-deep-research").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-06-26").build(),
        builder().id("o3-pro").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-06-10").build(),
        builder().id("o3").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-04-16").build(),
        builder().id("o3-deep-research").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-06-26").build(),
        builder().id("o3-mini").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-01-31").build(),
        builder().id("o1-pro").maxOutput(100000).contextWindowTokens(200000).releasedAt("2025-03-19").build(),
        builder().id("o1").maxOutput(100000).contextWindowTokens(200000).releasedAt("2024-12-17").build(),
        builder().id("gpt-4.1").maxOutput(32768).contextWindowTokens(1047576).releasedAt("2025-04-14").build(),
        builder().id("gpt-4.1-mini").maxOutput(32768).contextWindowTokens(1047576).releasedAt("2025-04-14").build(),
        builder().id("gpt-4.1-nano").maxOutput(32768).contextWindowTokens(1047576).releasedAt("2025-04-14").build(),
        builder().id("gpt-4o-mini").maxOutput(16384).contextWindowTokens(128000).releasedAt("2024-07-18").build(),
        builder().id("gpt-4o-mini-search-preview").maxOutput(16384).contextWindowTokens(128000).releasedAt("2025-03-11").build(),
        builder().id("gpt-4o").maxOutput(4096).contextWindowTokens(128000).releasedAt("2024-05-13").build(),
        builder().id("gpt-4o-search-preview").maxOutput(16384).contextWindowTokens(128000).releasedAt("2025-03-11").build(),
        builder().id("gpt-4o-2024-11-20").maxOutput(4096).contextWindowTokens(128000).releasedAt("2024-11-20").build(),
        builder().id("gpt-4o-2024-05-13").maxOutput(4096).contextWindowTokens(128000).releasedAt("2024-05-13").build(),
        builder().id("gpt-audio").maxOutput(16384).contextWindowTokens(128000).releasedAt("2025-08-28").build(),
        builder().id("gpt-4o-audio-preview").maxOutput(16384).contextWindowTokens(128000).releasedAt("2024-12-17").build(),
        builder().id("gpt-4o-mini-audio-preview").maxOutput(16384).contextWindowTokens(128000).releasedAt("2024-12-17").build(),
        builder().id("gpt-4-turbo").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("gpt-4-turbo-2024-04-09").maxOutput(4096).contextWindowTokens(128000).releasedAt("2024-04-09").build(),
        builder().id("gpt-4-turbo-preview").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("gpt-4-0125-preview").maxOutput(4096).contextWindowTokens(128000).releasedAt("2024-01-25").build(),
        builder().id("gpt-4-1106-preview").maxOutput(4096).contextWindowTokens(128000).releasedAt("2023-11-06").build(),
        builder().id("gpt-4").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("gpt-4-0613").maxOutput(4096).contextWindowTokens(8192).releasedAt("2023-06-13").build(),
        builder().id("gpt-3.5-turbo").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("gpt-3.5-turbo-0125").maxOutput(4096).contextWindowTokens(16384).releasedAt("2024-01-25").build(),
        builder().id("gpt-3.5-turbo-1106").maxOutput(4096).contextWindowTokens(16384).releasedAt("2023-11-06").build(),
        builder().id("gpt-3.5-turbo-instruct").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("computer-use-preview").maxOutput(1024).contextWindowTokens(8192).releasedAt("2025-03-11").build()
      )
      .build();
  }

  private OpenAiEndpoints() {}
}
