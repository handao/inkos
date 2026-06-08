package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class HunyuanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("hunyuan").label("腾讯混元").group("china").api("openai-completions")
      .baseUrl("https://api.hunyuan.cloud.tencent.com/v1")
      .checkModel("hunyuan-lite")
      .temperatureRange(0, 2).defaultTemperature(1.0).writingTemperature(1.0)
      .addModels(
        builder().id("hunyuan-2.0-thinking-20251109").maxOutput(64000).contextWindowTokens(128000).releasedAt("2025-11-09").build(),
        builder().id("hunyuan-2.0-instruct-20251111").maxOutput(16000).contextWindowTokens(128000).releasedAt("2025-11-11").build(),
        builder().id("hunyuan-a13b").maxOutput(32000).contextWindowTokens(256000).releasedAt("2025-06-25").build(),
        builder().id("hunyuan-t1-latest").maxOutput(64000).contextWindowTokens(96000).releasedAt("2025-08-22").build(),
        builder().id("hunyuan-t1-20250711").maxOutput(64000).contextWindowTokens(92000).releasedAt("2025-07-11").build(),
        builder().id("hunyuan-t1-20250529").maxOutput(64000).contextWindowTokens(92000).releasedAt("2025-05-29").build(),
        builder().id("hunyuan-t1-20250403").maxOutput(64000).contextWindowTokens(92000).releasedAt("2025-04-03").build(),
        builder().id("hunyuan-t1-20250321").maxOutput(64000).contextWindowTokens(92000).releasedAt("2025-03-21").build(),
        builder().id("hunyuan-lite").maxOutput(6000).contextWindowTokens(256000).releasedAt("2024-10-30").build(),
        builder().id("hunyuan-standard").maxOutput(2000).contextWindowTokens(32000).releasedAt("2025-02-10").build(),
        builder().id("hunyuan-standard-256K").maxOutput(6000).contextWindowTokens(256000).releasedAt("2025-02-10").build(),
        builder().id("hunyuan-large").maxOutput(4000).contextWindowTokens(32000).releasedAt("2025-02-10").build(),
        builder().id("hunyuan-large-longcontext").maxOutput(6000).contextWindowTokens(134000).releasedAt("2024-12-18").build(),
        builder().id("hunyuan-turbo-latest").maxOutput(4000).contextWindowTokens(32000).releasedAt("2025-01-10").build(),
        builder().id("hunyuan-turbo-20241223").maxOutput(4000).contextWindowTokens(32000).releasedAt("2025-01-10").build(),
        builder().id("hunyuan-turbos-longtext-128k-20250325").maxOutput(6000).contextWindowTokens(134000).releasedAt("2025-03-25").build(),
        builder().id("hunyuan-turbos-latest").maxOutput(16000).contextWindowTokens(44000).releasedAt("2025-07-16").build(),
        builder().id("hunyuan-vision-1.5-instruct").maxOutput(16000).contextWindowTokens(40000).releasedAt("2025-12-17").build(),
        builder().id("hunyuan-t1-vision-20250916").maxOutput(20000).contextWindowTokens(48000).releasedAt("2025-09-16").build(),
        builder().id("hunyuan-turbos-vision-video").maxOutput(8000).contextWindowTokens(32000).releasedAt("2025-07-28").build()
      )
      .build();
  }

  private HunyuanEndpoints() {}
}
