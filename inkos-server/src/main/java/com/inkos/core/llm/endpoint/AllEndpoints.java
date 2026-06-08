package com.inkos.core.llm.endpoint;

import com.inkos.core.llm.EndpointConfig;
import java.util.ArrayList;
import java.util.List;

public final class AllEndpoints {

  private static final List<EndpointConfig> ALL = loadAll();

  private static List<EndpointConfig> loadAll() {
    List<EndpointConfig> all = new ArrayList<>(43);
    all.add(OpenAiEndpoints.getEndpoint());
    all.add(AnthropicEndpoints.getEndpoint());
    all.add(GoogleEndpoints.getEndpoint());
    all.add(DeepSeekEndpoints.getEndpoint());
    all.add(MoonshotEndpoints.getEndpoint());
    all.add(ZhipuEndpoints.getEndpoint());
    all.add(MiniMaxEndpoints.getEndpoint());
    all.add(SiliconCloudEndpoints.getEndpoint());
    all.add(BailianEndpoints.getEndpoint());
    all.add(VolcengineEndpoints.getEndpoint());
    all.add(HunyuanEndpoints.getEndpoint());
    all.add(BaichuanEndpoints.getEndpoint());
    all.add(StepFunEndpoints.getEndpoint());
    all.add(WenXinEndpoints.getEndpoint());
    all.add(SparkEndpoints.getEndpoint());
    all.add(SenseNovaEndpoints.getEndpoint());
    all.add(TencentCloudEndpoints.getEndpoint());
    all.add(XiaomiMiMoEndpoints.getEndpoint());
    all.add(LongCatEndpoints.getEndpoint());
    all.add(InternLMEndpoints.getEndpoint());
    all.add(ZeroOneEndpoints.getEndpoint());
    all.add(Ai360Endpoints.getEndpoint());
    all.add(OllamaEndpoints.getEndpoint());
    all.add(OpenRouterEndpoints.getEndpoint());
    all.add(MistralEndpoints.getEndpoint());
    all.add(XaiEndpoints.getEndpoint());
    all.add(GitHubCopilotEndpoints.getEndpoint());
    all.add(KkAiApiEndpoints.getEndpoint());
    all.add(NewApiEndpoints.getEndpoint());
    all.add(CustomEndpoints.getEndpoint());
    all.add(KimiCodingPlanEndpoints.getEndpoint());
    all.add(KimiCodeEndpoints.getEndpoint());
    all.add(MiniMaxCodingPlanEndpoints.getEndpoint());
    all.add(BailianCodingPlanEndpoints.getEndpoint());
    all.add(GlmCodingPlanEndpoints.getEndpoint());
    all.add(VolcengineCodingPlanEndpoints.getEndpoint());
    all.add(OpenCodeCodingPlanEndpoints.getEndpoint());
    all.add(AstronCodingPlanEndpoints.getEndpoint());
    all.add(GiteeAiEndpoints.getEndpoint());
    all.add(InfiniAiEndpoints.getEndpoint());
    all.add(ModelScopeEndpoints.getEndpoint());
    all.add(PpioEndpoints.getEndpoint());
    all.add(QiniuEndpoints.getEndpoint());
    return List.copyOf(all);
  }

  public static List<EndpointConfig> getAll() {
    return ALL;
  }

  private AllEndpoints() {}
}
