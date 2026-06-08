package com.inkos.core.llm;

public enum LlmProviderType {
    OPENAI,
    ANTHROPIC,
    GOOGLE,
    DEEPSEEK,
    MOONSHOT,
    ZHIPU,
    MINIMAX,
    SILICONCLOUD,
    BAILIAN,
    VOLCENGINE,
    HUNYUAN,
    BAICHUAN,
    STEPFUN,
    WENXIN,
    SPARK,
    SENSENOVA,
    TENCENTCLOUD,
    XIAOMIMIMO,
    LONGCAT,
    INTERNLM,
    ZEROONE,
    AI360,
    OLLAMA,
    OPENROUTER,
    MISTRAL,
    XAI,
    GITHUB_COPILOT,
    KKAIAPI,
    NEWAPI,
    CUSTOM,
    KIMI_CODING_PLAN,
    KIMI_CODE,
    MINIMAX_CODING_PLAN,
    BAILIAN_CODING_PLAN,
    GLM_CODING_PLAN,
    VOLCENGINE_CODING_PLAN,
    OPENCODE_CODING_PLAN,
    ASTRON_CODING_PLAN,
    GITEEAI,
    INFINIAI,
    MODELSCOPE,
    PPIO,
    QINIU;

    public static LlmProviderType fromString(String value) {
        if (value == null) return CUSTOM;
        return switch (value.toLowerCase()) {
            case "openai" -> OPENAI;
            case "anthropic" -> ANTHROPIC;
            case "google" -> GOOGLE;
            case "deepseek" -> DEEPSEEK;
            case "moonshot" -> MOONSHOT;
            case "zhipu" -> ZHIPU;
            case "minimax" -> MINIMAX;
            case "siliconcloud" -> SILICONCLOUD;
            case "bailian" -> BAILIAN;
            case "volcengine" -> VOLCENGINE;
            case "hunyuan" -> HUNYUAN;
            case "baichuan" -> BAICHUAN;
            case "stepfun" -> STEPFUN;
            case "wenxin" -> WENXIN;
            case "spark" -> SPARK;
            case "sensenova" -> SENSENOVA;
            case "tencentcloud" -> TENCENTCLOUD;
            case "xiaomimimo" -> XIAOMIMIMO;
            case "longcat" -> LONGCAT;
            case "internlm" -> INTERNLM;
            case "zeroone" -> ZEROONE;
            case "ai360" -> AI360;
            case "ollama" -> OLLAMA;
            case "openrouter" -> OPENROUTER;
            case "mistral" -> MISTRAL;
            case "xai" -> XAI;
            case "githubcopilot" -> GITHUB_COPILOT;
            case "kkaiapi" -> KKAIAPI;
            case "newapi" -> NEWAPI;
            case "kimicodingplan" -> KIMI_CODING_PLAN;
            case "kimicode" -> KIMI_CODE;
            case "minimaxcodingplan" -> MINIMAX_CODING_PLAN;
            case "bailiancodingplan" -> BAILIAN_CODING_PLAN;
            case "glmcodingplan" -> GLM_CODING_PLAN;
            case "volcenginecodingplan" -> VOLCENGINE_CODING_PLAN;
            case "opencodecodingplan" -> OPENCODE_CODING_PLAN;
            case "astroncodingplan" -> ASTRON_CODING_PLAN;
            case "giteeai" -> GITEEAI;
            case "infiniai" -> INFINIAI;
            case "modelscope" -> MODELSCOPE;
            case "ppio" -> PPIO;
            case "qiniu" -> QINIU;
            default -> CUSTOM;
        };
    }

    public String toApiProtocol() {
        return switch (this) {
            case ANTHROPIC, BAILIAN, KIMI_CODING_PLAN, KIMI_CODE, MINIMAX_CODING_PLAN,
                 BAILIAN_CODING_PLAN, GLM_CODING_PLAN, VOLCENGINE_CODING_PLAN,
                 OPENCODE_CODING_PLAN, ASTRON_CODING_PLAN -> "anthropic-messages";
            case GOOGLE -> "google-generative-ai";
            case OPENAI, GITHUB_COPILOT, OPENROUTER -> "openai-responses";
            default -> "openai-completions";
        };
    }
}
