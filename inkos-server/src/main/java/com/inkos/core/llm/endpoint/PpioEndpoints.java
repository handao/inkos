package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class PpioEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("ppio").label("PPIO").group("aggregator").api("openai-completions")
      .baseUrl("https://api.ppinfra.com/v3/openai")
      .checkModel("deepseek/deepseek-v3.2")
      .temperatureRange(0, 2).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("deepseek/deepseek-v3.2").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-v3.2-exp").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-v3.1-terminus").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-v3.1").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-v3-0324").maxOutput(8192).contextWindowTokens(65536).build(),
        builder().id("deepseek/deepseek-v3-turbo").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-r1-0528").maxOutput(65536).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-r1-turbo").maxOutput(65536).contextWindowTokens(131072).build(),
        builder().id("deepseek/deepseek-prover-v2-671b").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("moonshotai/kimi-k2.6").maxOutput(32768).contextWindowTokens(262144).releasedAt("2026-04-21").temperature(1.0).build(),
        builder().id("moonshotai/kimi-k2.5").maxOutput(32768).contextWindowTokens(262144).releasedAt("2026-01-27").temperature(1.0).build(),
        builder().id("moonshotai/kimi-k2-thinking").maxOutput(65536).contextWindowTokens(262144).temperature(1.0).build(),
        builder().id("moonshotai/kimi-k2-0905").maxOutput(8192).contextWindowTokens(262144).build(),
        builder().id("moonshotai/kimi-k2-instruct").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("minimax/minimax-m2.7").maxOutput(32768).contextWindowTokens(204800).build(),
        builder().id("minimax/minimax-m2.7-highspeed").maxOutput(32768).contextWindowTokens(204800).build(),
        builder().id("minimax/minimax-m2.5").maxOutput(32768).contextWindowTokens(196608).build(),
        builder().id("minimax/minimax-m2.5-highspeed").maxOutput(32768).contextWindowTokens(196608).build(),
        builder().id("minimax/minimax-m2.1").maxOutput(32768).contextWindowTokens(204800).build(),
        builder().id("minimax/minimax-m2").maxOutput(32768).contextWindowTokens(204800).build(),
        builder().id("zai-org/glm-5.1").maxOutput(16384).contextWindowTokens(202752).releasedAt("2026-04-23").build(),
        builder().id("zai-org/glm-5").maxOutput(16384).contextWindowTokens(202752).build(),
        builder().id("zai-org/glm-5-turbo").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("zai-org/glm-4.7").maxOutput(16384).contextWindowTokens(202752).build(),
        builder().id("zai-org/glm-4.7-flash").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("zai-org/glm-4.6").maxOutput(16384).contextWindowTokens(202752).build(),
        builder().id("zai-org/glm-4.5").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("zai-org/glm-4.5-air").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3.6-27b").maxOutput(65536).contextWindowTokens(262144).releasedAt("2026-04-23").build(),
        builder().id("qwen/qwen3.5-plus").maxOutput(65536).contextWindowTokens(1000000).build(),
        builder().id("qwen/qwen3.5-397b-a17b").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("qwen/qwen3.5-122b-a10b").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("qwen/qwen3.5-35b-a3b").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("qwen/qwen3.5-27b").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("qwen/qwen3-coder-next").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("qwen/qwen3-coder-480b-a35b-instruct").maxOutput(65536).contextWindowTokens(262144).build(),
        builder().id("qwen/qwen3-next-80b-a3b-instruct").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3-next-80b-a3b-thinking").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3-235b-a22b-instruct-2507").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3-235b-a22b-thinking-2507").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3-235b-a22b-fp8").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3-32b-fp8").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen3-30b-a3b-fp8").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("qwen/qwen-2.5-72b-instruct").maxOutput(8192).contextWindowTokens(32768).build(),
        builder().id("baidu/ernie-4.5-300b-a47b-paddle").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("baidu/ernie-4.5-21B-a3b").maxOutput(8192).contextWindowTokens(131072).build(),
        builder().id("baidu/ernie-4.5-21b-a3b-thinking").maxOutput(32768).contextWindowTokens(131072).build(),
        builder().id("xiaomimimo/mimo-v2-pro").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("xiaomimimo/mimo-v2-flash").maxOutput(16384).contextWindowTokens(131072).build(),
        builder().id("ppio-4b").maxOutput(8192).contextWindowTokens(32768).build(),
        builder().id("kat-coder").maxOutput(32768).contextWindowTokens(131072).build()
      )
      .build();
  }

  private PpioEndpoints() {}
}
