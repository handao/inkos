package com.inkos.core.llm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LlmProviderTypeTest {

  @Test
  void fromString_shouldReturnCorrectEnum() {
    assertEquals(LlmProviderType.OPENAI, LlmProviderType.fromString("openai"));
    assertEquals(LlmProviderType.ANTHROPIC, LlmProviderType.fromString("anthropic"));
    assertEquals(LlmProviderType.GOOGLE, LlmProviderType.fromString("google"));
    assertEquals(LlmProviderType.DEEPSEEK, LlmProviderType.fromString("deepseek"));
    assertEquals(LlmProviderType.MOONSHOT, LlmProviderType.fromString("moonshot"));
    assertEquals(LlmProviderType.ZHIPU, LlmProviderType.fromString("zhipu"));
    assertEquals(LlmProviderType.MINIMAX, LlmProviderType.fromString("minimax"));
    assertEquals(LlmProviderType.SILICONCLOUD, LlmProviderType.fromString("siliconcloud"));
    assertEquals(LlmProviderType.BAILIAN, LlmProviderType.fromString("bailian"));
    assertEquals(LlmProviderType.VOLCENGINE, LlmProviderType.fromString("volcengine"));
    assertEquals(LlmProviderType.HUNYUAN, LlmProviderType.fromString("hunyuan"));
    assertEquals(LlmProviderType.BAICHUAN, LlmProviderType.fromString("baichuan"));
    assertEquals(LlmProviderType.STEPFUN, LlmProviderType.fromString("stepfun"));
    assertEquals(LlmProviderType.WENXIN, LlmProviderType.fromString("wenxin"));
    assertEquals(LlmProviderType.SPARK, LlmProviderType.fromString("spark"));
    assertEquals(LlmProviderType.SENSENOVA, LlmProviderType.fromString("sensenova"));
    assertEquals(LlmProviderType.TENCENTCLOUD, LlmProviderType.fromString("tencentcloud"));
    assertEquals(LlmProviderType.XIAOMIMIMO, LlmProviderType.fromString("xiaomimimo"));
    assertEquals(LlmProviderType.LONGCAT, LlmProviderType.fromString("longcat"));
    assertEquals(LlmProviderType.INTERNLM, LlmProviderType.fromString("internlm"));
    assertEquals(LlmProviderType.ZEROONE, LlmProviderType.fromString("zeroone"));
    assertEquals(LlmProviderType.AI360, LlmProviderType.fromString("ai360"));
    assertEquals(LlmProviderType.OLLAMA, LlmProviderType.fromString("ollama"));
    assertEquals(LlmProviderType.OPENROUTER, LlmProviderType.fromString("openrouter"));
    assertEquals(LlmProviderType.MISTRAL, LlmProviderType.fromString("mistral"));
    assertEquals(LlmProviderType.XAI, LlmProviderType.fromString("xai"));
    assertEquals(LlmProviderType.GITHUB_COPILOT, LlmProviderType.fromString("githubcopilot"));
    assertEquals(LlmProviderType.KKAIAPI, LlmProviderType.fromString("kkaiapi"));
    assertEquals(LlmProviderType.NEWAPI, LlmProviderType.fromString("newapi"));
    assertEquals(LlmProviderType.KIMI_CODING_PLAN, LlmProviderType.fromString("kimicodingplan"));
    assertEquals(LlmProviderType.KIMI_CODE, LlmProviderType.fromString("kimicode"));
    assertEquals(LlmProviderType.MINIMAX_CODING_PLAN, LlmProviderType.fromString("minimaxcodingplan"));
    assertEquals(LlmProviderType.BAILIAN_CODING_PLAN, LlmProviderType.fromString("bailiancodingplan"));
    assertEquals(LlmProviderType.GLM_CODING_PLAN, LlmProviderType.fromString("glmcodingplan"));
    assertEquals(LlmProviderType.VOLCENGINE_CODING_PLAN, LlmProviderType.fromString("volcenginecodingplan"));
    assertEquals(LlmProviderType.OPENCODE_CODING_PLAN, LlmProviderType.fromString("opencodecodingplan"));
    assertEquals(LlmProviderType.ASTRON_CODING_PLAN, LlmProviderType.fromString("astroncodingplan"));
    assertEquals(LlmProviderType.GITEEAI, LlmProviderType.fromString("giteeai"));
    assertEquals(LlmProviderType.INFINIAI, LlmProviderType.fromString("infiniai"));
    assertEquals(LlmProviderType.MODELSCOPE, LlmProviderType.fromString("modelscope"));
    assertEquals(LlmProviderType.PPIO, LlmProviderType.fromString("ppio"));
    assertEquals(LlmProviderType.QINIU, LlmProviderType.fromString("qiniu"));
  }

  @Test
  void fromString_shouldReturnCustomForUnknown() {
    assertEquals(LlmProviderType.CUSTOM, LlmProviderType.fromString("nonexistent"));
    assertEquals(LlmProviderType.CUSTOM, LlmProviderType.fromString(""));
  }

  @Test
  void fromString_shouldReturnCustomForNull() {
    assertEquals(LlmProviderType.CUSTOM, LlmProviderType.fromString(null));
  }

  @Test
  void toApiProtocol_shouldReturnAnthropicForAnthropicLike() {
    assertEquals("anthropic-messages", LlmProviderType.ANTHROPIC.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.BAILIAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.KIMI_CODING_PLAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.KIMI_CODE.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.MINIMAX_CODING_PLAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.BAILIAN_CODING_PLAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.GLM_CODING_PLAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.VOLCENGINE_CODING_PLAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.OPENCODE_CODING_PLAN.toApiProtocol());
    assertEquals("anthropic-messages", LlmProviderType.ASTRON_CODING_PLAN.toApiProtocol());
  }

  @Test
  void toApiProtocol_shouldReturnGoogleForGoogle() {
    assertEquals("google-generative-ai", LlmProviderType.GOOGLE.toApiProtocol());
  }

  @Test
  void toApiProtocol_shouldReturnOpenaiResponsesForOpenAiLike() {
    assertEquals("openai-responses", LlmProviderType.OPENAI.toApiProtocol());
    assertEquals("openai-responses", LlmProviderType.GITHUB_COPILOT.toApiProtocol());
    assertEquals("openai-responses", LlmProviderType.OPENROUTER.toApiProtocol());
  }

  @Test
  void toApiProtocol_shouldReturnOpenaiCompletionsByDefault() {
    assertEquals("openai-completions", LlmProviderType.DEEPSEEK.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.MOONSHOT.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.ZHIPU.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.MINIMAX.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.SILICONCLOUD.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.OLLAMA.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.MISTRAL.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.XAI.toApiProtocol());
    assertEquals("openai-completions", LlmProviderType.CUSTOM.toApiProtocol());
  }

  @Test
  void enumShouldHaveCorrectCount() {
    assertEquals(43, LlmProviderType.values().length);
  }
}
