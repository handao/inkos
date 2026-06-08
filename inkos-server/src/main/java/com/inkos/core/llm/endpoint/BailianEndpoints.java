package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class BailianEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("bailian").label("百炼 (通义千问)").group("china").api("anthropic-messages")
      .baseUrl("https://dashscope.aliyuncs.com/apps/anthropic")
      .checkModel("qwen-turbo")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("qwen3.6-max-preview").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-04-21").build(),
        builder().id("qwen3.6-plus").maxOutput(65536).contextWindowTokens(1000000).releasedAt("2026-04-09").build(),
        builder().id("qwen3.6-flash").maxOutput(65536).contextWindowTokens(1000000).releasedAt("2026-04-17").build(),
        builder().id("qwen3.6-27b").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-04-23").build(),
        builder().id("qwen3.5-plus").maxOutput(65536).contextWindowTokens(1000000).releasedAt("2026-02-15").build(),
        builder().id("qwen3.5-flash").maxOutput(65536).contextWindowTokens(1000000).releasedAt("2026-02-24").build(),
        builder().id("qwen3.5-397b-a17b").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-02-16").build(),
        builder().id("qwen3.5-122b-a10b").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-02-24").build(),
        builder().id("qwen3.5-35b-a3b").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-02-24").build(),
        builder().id("qwen3.5-27b").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-02-24").build(),
        builder().id("qwen3-max").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-01-23").build(),
        builder().id("qwen3-max-preview").maxOutput(65536).contextWindowTokens(262144).releasedAt("2025-10-30").build(),
        builder().id("qwen-max").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen-plus").maxOutput(32768).contextWindowTokens(1000000).build(),
        builder().id("qwen-flash").maxOutput(32768).contextWindowTokens(1000000).releasedAt("2025-07-28").build(),
        builder().id("qwen-turbo").maxOutput(16384).contextWindowTokens(1000000).releasedAt("2025-07-15").build(),
        builder().id("kimi-k2.5").maxOutput(32768).contextWindowTokens(262144).temperature(1.0).build(),
        builder().id("kimi-k2-thinking").maxOutput(16384).contextWindowTokens(262144).releasedAt("2025-11-10").temperature(1.0).build(),
        builder().id("MiniMax-M2.5").maxOutput(32768).contextWindowTokens(196608).build(),
        builder().id("MiniMax-M2.1").maxOutput(32768).contextWindowTokens(204800).build(),
        builder().id("glm-5.1").maxOutput(16384).contextWindowTokens(202752).releasedAt("2026-04-23").build(),
        builder().id("glm-5").maxOutput(16384).contextWindowTokens(202752).build(),
        builder().id("glm-4.7").maxOutput(16384).contextWindowTokens(202752).build(),
        builder().id("glm-4.6").maxOutput(16384).contextWindowTokens(202752).build()
      )
      .build();
  }

  private BailianEndpoints() {}
}
