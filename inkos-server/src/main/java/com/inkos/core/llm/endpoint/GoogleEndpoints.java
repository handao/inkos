package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import com.inkos.core.llm.EndpointConfig.ModelCard.Capabilities;
import java.util.List;

public final class GoogleEndpoints {

  public static EndpointConfig getEndpoint() {
    var nonText = Capabilities.builder().text(false).imageOutput(true).build();
    return EndpointConfig.builder()
      .id("google").label("Google Gemini").group("overseas").api("google-generative-ai")
      .baseUrl("https://generativelanguage.googleapis.com/v1beta")
      .modelsBaseUrl("https://generativelanguage.googleapis.com/v1beta/openai")
      .checkModel("gemini-2.5-flash")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.0)
      .addModels(
        builder().id("gemini-pro-latest").maxOutput(65536).contextWindowTokens(1114112).build(),
        builder().id("gemini-flash-latest").maxOutput(65536).contextWindowTokens(1114112).build(),
        builder().id("gemini-flash-lite-latest").maxOutput(65536).contextWindowTokens(1114112).build(),
        builder().id("gemini-3.1-flash-image-preview").maxOutput(32768).contextWindowTokens(163840).enabled(false).status("nonText").capabilities(nonText).releasedAt("2026-02-26").build(),
        builder().id("gemini-3.1-pro-preview").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2026-02-19").build(),
        builder().id("gemini-3.1-flash-lite-preview").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2026-03-04").build(),
        builder().id("gemini-3-flash-preview").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2025-12-17").build(),
        builder().id("gemini-3-pro-image-preview").maxOutput(32768).contextWindowTokens(163840).enabled(false).status("nonText").capabilities(nonText).releasedAt("2025-11-20").build(),
        builder().id("gemini-2.5-pro").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2025-06-17").build(),
        builder().id("gemini-2.5-flash").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2025-06-17").build(),
        builder().id("gemini-2.5-flash-image").maxOutput(32768).contextWindowTokens(98304).enabled(false).status("nonText").capabilities(nonText).releasedAt("2025-08-26").build(),
        builder().id("gemini-2.5-flash-lite").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2025-07-22").build(),
        builder().id("gemini-2.5-flash-lite-preview-09-2025").maxOutput(65536).contextWindowTokens(1114112).releasedAt("2025-09-25").build(),
        builder().id("gemini-2.0-flash").maxOutput(8192).contextWindowTokens(1056768).releasedAt("2025-02-05").build(),
        builder().id("gemini-2.0-flash-001").maxOutput(8192).contextWindowTokens(1056768).releasedAt("2025-02-05").build(),
        builder().id("gemini-2.0-flash-lite").maxOutput(8192).contextWindowTokens(1056768).releasedAt("2025-02-05").build(),
        builder().id("gemini-2.0-flash-lite-001").maxOutput(8192).contextWindowTokens(1056768).releasedAt("2025-02-05").build(),
        builder().id("gemini-1.5-flash-002").maxOutput(8192).contextWindowTokens(1008192).releasedAt("2024-09-25").build(),
        builder().id("gemini-1.5-pro-002").maxOutput(8192).contextWindowTokens(2008192).releasedAt("2024-09-24").build(),
        builder().id("gemini-1.5-flash-8b-latest").maxOutput(8192).contextWindowTokens(1008192).releasedAt("2024-10-03").build(),
        builder().id("gemma-3-1b-it").maxOutput(8192).contextWindowTokens(40960).build(),
        builder().id("gemma-3-4b-it").maxOutput(8192).contextWindowTokens(40960).build(),
        builder().id("gemma-3-12b-it").maxOutput(8192).contextWindowTokens(40960).build(),
        builder().id("gemma-3-27b-it").maxOutput(8192).contextWindowTokens(139264).build(),
        builder().id("gemma-3n-e2b-it").maxOutput(2048).contextWindowTokens(10240).build(),
        builder().id("gemma-3n-e4b-it").maxOutput(2048).contextWindowTokens(10240).build()
      )
      .build();
  }

  private GoogleEndpoints() {}
}
