package com.inkos.core.llm;

import java.util.Map;
import java.util.Optional;

public record LlmProviderConfig(
    String provider,
    String service,
    String configSource,
    String baseUrl,
    String apiKey,
    String model,
    String proxyUrl,
    double temperature,
    int thinkingBudget,
    String apiFormat,
    boolean stream,
    Map<String, Object> extra,
    Map<String, String> headers,
    String defaultModel,
    int maxTokens
) {
    public static final String CONFIG_SOURCE_ENV = "env";
    public static final String CONFIG_SOURCE_STUDIO = "studio";

    public static final String API_FORMAT_CHAT = "chat";
    public static final String API_FORMAT_RESPONSES = "responses";

    public static Builder builder() {
        return new Builder();
    }

    public LlmProviderType resolveType() {
        return LlmProviderType.fromString(provider);
    }

    public static class Builder {
        private String provider = "openai";
        private String service = "custom";
        private String configSource = "env";
        private String baseUrl;
        private String apiKey = "";
        private String model;
        private String proxyUrl;
        private double temperature = 0.7;
        private int thinkingBudget = 0;
        private String apiFormat = "chat";
        private boolean stream = true;
        private Map<String, Object> extra = Map.of();
        private Map<String, String> headers = Map.of();
        private String defaultModel;
        private int maxTokens = 4096;

        public Builder provider(String provider) { this.provider = provider; return this; }
        public Builder service(String service) { this.service = service; return this; }
        public Builder configSource(String configSource) { this.configSource = configSource; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder proxyUrl(String proxyUrl) { this.proxyUrl = proxyUrl; return this; }
        public Builder temperature(double temperature) { this.temperature = temperature; return this; }
        public Builder thinkingBudget(int thinkingBudget) { this.thinkingBudget = thinkingBudget; return this; }
        public Builder apiFormat(String apiFormat) { this.apiFormat = apiFormat; return this; }
        public Builder stream(boolean stream) { this.stream = stream; return this; }
        public Builder extra(Map<String, Object> extra) { this.extra = extra; return this; }
        public Builder headers(Map<String, String> headers) { this.headers = headers; return this; }
        public Builder defaultModel(String defaultModel) { this.defaultModel = defaultModel; return this; }
        public Builder maxTokens(int maxTokens) { this.maxTokens = maxTokens; return this; }

        public LlmProviderConfig build() {
            return new LlmProviderConfig(provider, service, configSource, baseUrl, apiKey, model,
                proxyUrl, temperature, thinkingBudget, apiFormat, stream, extra, headers, defaultModel, maxTokens);
        }
    }
}
