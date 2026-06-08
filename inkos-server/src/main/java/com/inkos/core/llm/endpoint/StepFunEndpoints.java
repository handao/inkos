package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class StepFunEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("stepfun").label("阶跃星辰").group("china").api("openai-completions")
      .baseUrl("https://api.stepfun.com/v1")
      .checkModel("step-1-8k")
      .temperatureRange(0, 1).defaultTemperature(0.7).writingTemperature(1.0)
      .addModels(
        builder().id("step-3.5-flash").maxOutput(4096).contextWindowTokens(256000).build(),
        builder().id("step-3").maxOutput(4096).contextWindowTokens(64000).build(),
        builder().id("step-r1-v-mini").maxOutput(4096).contextWindowTokens(100000).build(),
        builder().id("step-1-8k").maxOutput(4096).contextWindowTokens(8000).build(),
        builder().id("step-1-32k").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("step-1-256k").maxOutput(4096).contextWindowTokens(256000).build(),
        builder().id("step-2-mini").maxOutput(4096).contextWindowTokens(8000).releasedAt("2025-01-14").build(),
        builder().id("step-2-16k").maxOutput(4096).contextWindowTokens(16000).build(),
        builder().id("step-2-16k-exp").maxOutput(4096).contextWindowTokens(16000).releasedAt("2025-01-15").build(),
        builder().id("step-1v-8k").maxOutput(4096).contextWindowTokens(8000).build(),
        builder().id("step-1v-32k").maxOutput(4096).contextWindowTokens(32000).build(),
        builder().id("step-1o-vision-32k").maxOutput(4096).contextWindowTokens(32000).releasedAt("2025-01-22").build(),
        builder().id("step-1o-turbo-vision").maxOutput(4096).contextWindowTokens(32000).releasedAt("2025-02-14").build()
      )
      .build();
  }

  private StepFunEndpoints() {}
}
