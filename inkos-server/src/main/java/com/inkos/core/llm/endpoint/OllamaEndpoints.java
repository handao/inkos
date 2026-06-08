package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class OllamaEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("ollama").label("Ollama (本地)").group("local").api("openai-completions")
      .baseUrl("http://localhost:11434/v1")
      .checkModel("llama3.2:3b")
      .addModels(
        builder().id("deepseek-v3.1:671b").maxOutput(4096).contextWindowTokens(163840).build(),
        builder().id("gpt-oss:20b").maxOutput(4096).contextWindowTokens(131072).releasedAt("2025-08-05").build(),
        builder().id("gpt-oss:120b").maxOutput(4096).contextWindowTokens(131072).releasedAt("2025-08-05").build(),
        builder().id("qwen3-coder:480b").maxOutput(4096).contextWindowTokens(262144).build(),
        builder().id("deepseek-r1").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("deepseek-v3").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("llama3.1").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("llama3.1:70b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("llama3.1:405b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("codellama").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("codellama:13b").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("codellama:34b").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("codellama:70b").maxOutput(4096).contextWindowTokens(16384).build(),
        builder().id("qwq").maxOutput(4096).contextWindowTokens(128000).releasedAt("2024-11-28").build(),
        builder().id("qwen3").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("qwen2.5:0.5b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("qwen2.5:1.5b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("qwen2.5").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("qwen2.5:72b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("codeqwen").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("qwen2:0.5b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("qwen2:1.5b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("qwen2").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("qwen2:72b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("gemma2:2b").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("gemma2").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("gemma2:27b").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("codegemma:2b").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("codegemma").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("phi3").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("phi3:14b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("wizardlm2").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("wizardlm2:8x22b").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("mathstral").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("mistral").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("mixtral").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("mixtral:8x22b").maxOutput(4096).contextWindowTokens(65536).build(),
        builder().id("mistral-large").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("mistral-nemo").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("codestral").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("aya").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("aya:35b").maxOutput(4096).contextWindowTokens(8192).build(),
        builder().id("command-r").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("command-r-plus").maxOutput(4096).contextWindowTokens(131072).build(),
        builder().id("deepseek-v2").maxOutput(4096).contextWindowTokens(32768).build(),
        builder().id("deepseek-v2:236b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("deepseek-coder-v2").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("deepseek-coder-v2:236b").maxOutput(4096).contextWindowTokens(128000).build(),
        builder().id("llava").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("llava:13b").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("llava:34b").maxOutput(4096).contextWindowTokens(4096).build(),
        builder().id("minicpm-v").maxOutput(4096).contextWindowTokens(128000).build()
      )
      .build();
  }

  private OllamaEndpoints() {}
}
