package com.inkos.core.llm;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LlmServiceResolver {

  private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
  private final EndpointRegistry endpointRegistry;
  private final Map<String, AgentModelOverride> agentOverrides = new ConcurrentHashMap<>();

  public LlmServiceResolver(EndpointRegistry endpointRegistry) {
    this.endpointRegistry = endpointRegistry;
  }

  public void registerProvider(String serviceName, LlmProvider provider) {
    providers.put(serviceName, provider);
  }

  public void setAgentOverrides(Map<String, AgentModelOverride> overrides) {
    agentOverrides.clear();
    agentOverrides.putAll(overrides);
  }

  public void setAgentOverride(String agentName, AgentModelOverride override) {
    agentOverrides.put(agentName, override);
  }

  public Optional<AgentModelOverride> getAgentOverride(String agentName) {
    return Optional.ofNullable(agentOverrides.get(agentName));
  }

  public ResolvedProvider resolveProvider(LlmProviderConfig config) {
    String serviceName = config.service() != null ? config.service() : "custom";

    LlmProvider cached = providers.get(serviceName);
    if (cached != null) {
      return new ResolvedProvider(cached, resolveModel(config));
    }

    LlmProviderType type = config.resolveType();
    return findBuiltinProvider(type, serviceName, config.apiKey())
      .map(provider -> new ResolvedProvider(provider, resolveModel(config)))
      .orElseThrow(() -> new IllegalStateException(
        "No provider found for service: " + serviceName
      ));
  }

  public ResolvedProvider resolveProviderForAgent(
    LlmProviderConfig baseConfig,
    String agentName
  ) {
    var override = agentOverrides.get(agentName);
    if (override == null) {
      return resolveProvider(baseConfig);
    }

    var config = LlmProviderConfig.builder()
      .provider(override.provider() != null ? override.provider() : baseConfig.provider())
      .service(override.service() != null ? override.service() : baseConfig.service())
      .configSource(baseConfig.configSource())
      .baseUrl(override.baseUrl() != null ? override.baseUrl() : baseConfig.baseUrl())
      .apiKey(override.apiKey() != null ? override.apiKey() : baseConfig.apiKey())
      .model(override.model() != null ? override.model() : baseConfig.model())
      .temperature(override.temperature() > 0 ? override.temperature() : baseConfig.temperature())
      .maxTokens(override.maxTokens() > 0 ? override.maxTokens() : 16384)
      .stream(baseConfig.stream())
      .build();

    return resolveProvider(config);
  }

  private Optional<LlmProvider> findBuiltinProvider(LlmProviderType type, String serviceName, String apiKey) {
    EndpointConfig endpoint = endpointRegistry.getEndpoint(serviceName);
    if (endpoint != null) {
      return Optional.of(new GenericLlmProvider(endpoint, type, apiKey));
    }

    String group = type.name().toLowerCase();
    List<EndpointConfig> groupEndpoints = endpointRegistry.getEndpointsByGroup(group);
    if (!groupEndpoints.isEmpty()) {
      return Optional.of(new GenericLlmProvider(groupEndpoints.getFirst(), type, apiKey));
    }

    return Optional.empty();
  }

  public ResolvedModel resolveModel(LlmProviderConfig config) {
    String serviceName = config.service() != null ? config.service() : "custom";
    EndpointConfig endpoint = endpointRegistry.getEndpoint(serviceName);

    int maxTokens = 16384;
    int contextWindow = 128000;

    if (endpoint != null) {
      EndpointConfig.ModelCard modelCard = endpoint.findModel(config.model()).orElse(null);
      if (modelCard != null) {
        maxTokens = modelCard.maxOutput();
        contextWindow = modelCard.contextWindowTokens();
      }
    }

    return new ResolvedModel(
      config.model(),
      config.baseUrl() != null ? config.baseUrl() :
        endpoint != null ? endpoint.baseUrl() : "",
      config.apiKey() != null ? config.apiKey() : "",
      config.temperature() > 0 ? config.temperature() : 0.7,
      maxTokens,
      contextWindow
    );
  }

  public record ResolvedModel(
    String modelId,
    String baseUrl,
    String apiKey,
    double temperature,
    int maxTokens,
    int contextWindow
  ) {}

  public record ResolvedProvider(
    LlmProvider provider,
    ResolvedModel model
  ) {}

  public record AgentModelOverride(
    String provider,
    String service,
    String baseUrl,
    String apiKey,
    String model,
    double temperature,
    int maxTokens
  ) {
    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private String provider;
      private String service;
      private String baseUrl;
      private String apiKey;
      private String model;
      private double temperature = -1;
      private int maxTokens = -1;

      public Builder provider(String val) { this.provider = val; return this; }
      public Builder service(String val) { this.service = val; return this; }
      public Builder baseUrl(String val) { this.baseUrl = val; return this; }
      public Builder apiKey(String val) { this.apiKey = val; return this; }
      public Builder model(String val) { this.model = val; return this; }
      public Builder temperature(double val) { this.temperature = val; return this; }
      public Builder maxTokens(int val) { this.maxTokens = val; return this; }

      public AgentModelOverride build() {
        return new AgentModelOverride(provider, service, baseUrl, apiKey, model, temperature, maxTokens);
      }
    }
  }
}
