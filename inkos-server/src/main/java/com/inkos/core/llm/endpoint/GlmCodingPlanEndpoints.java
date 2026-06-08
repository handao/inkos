package com.inkos.core.llm.endpoint;

import static com.inkos.core.llm.EndpointConfig.ModelCard.builder;
import com.inkos.core.llm.EndpointConfig;
import java.util.List;

public final class GlmCodingPlanEndpoints {

  public static EndpointConfig getEndpoint() {
    return EndpointConfig.builder()
      .id("glmCodingPlan").label("GLM Coding Plan").group("codingPlan").api("anthropic-messages")
      .baseUrl("https://api.z.ai/api/anthropic")
      .checkModel("glm-5.1")
      .temperatureRange(0, 1).defaultTemperature(0.95).writingTemperature(0.95)
      .addModels(
        builder().id("GLM-5.1").maxOutput(131072).contextWindowTokens(204800).releasedAt("2026-03-27").build(),
        builder().id("GLM-5").maxOutput(131072).contextWindowTokens(200000).releasedAt("2026-02-12").build(),
        builder().id("GLM-5-Turbo").maxOutput(131072).contextWindowTokens(200000).releasedAt("2026-02-12").build(),
        builder().id("GLM-4.7").maxOutput(131072).contextWindowTokens(200000).releasedAt("2025-12-01").build(),
        builder().id("GLM-4.6").maxOutput(65536).contextWindowTokens(202752).releasedAt("2025-12-01").build(),
        builder().id("GLM-4.5").maxOutput(65536).contextWindowTokens(202752).releasedAt("2025-12-01").build(),
        builder().id("GLM-4.5-Air").maxOutput(65536).contextWindowTokens(202752).releasedAt("2025-12-01").build()
      )
      .build();
  }

  private GlmCodingPlanEndpoints() {}
}
