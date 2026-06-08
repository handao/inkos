package com.inkos.core.llm;

import com.inkos.core.llm.endpoint.AllEndpoints;
import java.util.*;

public class EndpointRegistry {

  private final Map<String, EndpointConfig> endpoints = new LinkedHashMap<>();

  public EndpointRegistry() {
    for (EndpointConfig ep : AllEndpoints.getAll()) {
      endpoints.put(ep.id(), ep);
    }
  }

  public void register(EndpointConfig endpoint) {
    endpoints.put(endpoint.id(), endpoint);
  }

  public EndpointConfig getEndpoint(String id) {
    return endpoints.get(id);
  }

  public List<EndpointConfig> getAllEndpoints() {
    return List.copyOf(endpoints.values());
  }

  public List<EndpointConfig> getEndpointsByGroup(String group) {
    return endpoints.values().stream()
      .filter(e -> group.equals(e.group()))
      .toList();
  }

  public EndpointConfig.ModelCard lookupModel(String serviceId, String modelId) {
    String lower = modelId.toLowerCase();

    EndpointConfig exact = endpoints.get(serviceId);
    if (exact != null) {
      Optional<EndpointConfig.ModelCard> hit = exact.findModel(modelId);
      if (hit.isPresent()) return hit.get();
    }

    List<EndpointConfig.ModelCard> matches = new ArrayList<>();
    for (EndpointConfig ep : endpoints.values()) {
      Optional<EndpointConfig.ModelCard> hit = ep.findModel(modelId);
      hit.ifPresent(matches::add);
    }

    if (matches.isEmpty()) return null;

    String[] priority = {
      "anthropic", "openai", "google", "deepseek", "bailian",
      "moonshot", "kimicode", "zhipu", "minimax", "xai",
      "siliconcloud", "openrouter"
    };
    Set<String> prioritySet = new HashSet<>(List.of(priority));

    matches.sort(Comparator.comparingInt(a ->
      prioritySet.contains(a.id()) ? List.of(priority).indexOf(a.id()) : 999));

    return matches.getFirst();
  }

  public int size() {
    return endpoints.size();
  }
}
