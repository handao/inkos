package com.inkos.core.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record EndpointConfig(
    String id,
    String label,
    String group,
    String api,
    String baseUrl,
    String modelsBaseUrl,
    String checkModel,
    double[] temperatureRange,
    Double defaultTemperature,
    Double writingTemperature,
    String temperatureHint,
    ProviderCompat compat,
    TransportDefaults transportDefaults,
    List<ModelCard> models
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String label;
        private String group;
        private String api = "openai-completions";
        private String baseUrl;
        private String modelsBaseUrl;
        private String checkModel;
        private double[] temperatureRange;
        private Double defaultTemperature;
        private Double writingTemperature;
        private String temperatureHint;
        private ProviderCompat compat;
        private TransportDefaults transportDefaults;
        private final List<ModelCard> models = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder label(String label) { this.label = label; return this; }
        public Builder group(String group) { this.group = group; return this; }
        public Builder api(String api) { this.api = api; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder modelsBaseUrl(String modelsBaseUrl) { this.modelsBaseUrl = modelsBaseUrl; return this; }
        public Builder checkModel(String checkModel) { this.checkModel = checkModel; return this; }
        public Builder temperatureRange(double min, double max) { this.temperatureRange = new double[]{min, max}; return this; }
        public Builder defaultTemperature(Double val) { this.defaultTemperature = val; return this; }
        public Builder writingTemperature(Double val) { this.writingTemperature = val; return this; }
        public Builder temperatureHint(String val) { this.temperatureHint = val; return this; }
        public Builder compat(ProviderCompat val) { this.compat = val; return this; }
        public Builder transportDefaults(TransportDefaults val) { this.transportDefaults = val; return this; }
        public Builder addModel(ModelCard model) { this.models.add(model); return this; }
        public Builder addModels(ModelCard... models) { this.models.addAll(List.of(models)); return this; }

        public EndpointConfig build() {
            return new EndpointConfig(
                id, label, group, api, baseUrl, modelsBaseUrl, checkModel,
                temperatureRange, defaultTemperature, writingTemperature,
                temperatureHint, compat, transportDefaults, List.copyOf(models)
            );
        }
    }

    public record ModelCard(
        String id,
        int maxOutput,
        int contextWindowTokens,
        boolean enabled,
        String deploymentName,
        String releasedAt,
        Double temperature,
        String status,
        String replacement,
        Capabilities capabilities
    ) {
        public static ModelCardBuilder builder() {
            return new ModelCardBuilder();
        }

        public static class ModelCardBuilder {
            private String id;
            private int maxOutput;
            private int contextWindowTokens;
            private boolean enabled = true;
            private String deploymentName;
            private String releasedAt;
            private Double temperature;
            private String status;
            private String replacement;
            private Capabilities capabilities;

            public ModelCardBuilder id(String id) { this.id = id; return this; }
            public ModelCardBuilder maxOutput(int val) { this.maxOutput = val; return this; }
            public ModelCardBuilder contextWindowTokens(int val) { this.contextWindowTokens = val; return this; }
            public ModelCardBuilder enabled(boolean val) { this.enabled = val; return this; }
            public ModelCardBuilder deploymentName(String val) { this.deploymentName = val; return this; }
            public ModelCardBuilder releasedAt(String val) { this.releasedAt = val; return this; }
            public ModelCardBuilder temperature(Double val) { this.temperature = val; return this; }
            public ModelCardBuilder status(String val) { this.status = val; return this; }
            public ModelCardBuilder replacement(String val) { this.replacement = val; return this; }
            public ModelCardBuilder capabilities(Capabilities val) { this.capabilities = val; return this; }

            public ModelCard build() {
                return new ModelCard(id, maxOutput, contextWindowTokens, enabled, deploymentName,
                    releasedAt, temperature, status, replacement, capabilities);
            }
        }

        public record Capabilities(
            Boolean text,
            Boolean imageInput,
            Boolean imageOutput,
            Boolean tools,
            Boolean reasoning
        ) {
            public static CapabilitiesBuilder builder() {
                return new CapabilitiesBuilder();
            }

            public static class CapabilitiesBuilder {
                private Boolean text;
                private Boolean imageInput;
                private Boolean imageOutput;
                private Boolean tools;
                private Boolean reasoning;

                public CapabilitiesBuilder text(Boolean val) { this.text = val; return this; }
                public CapabilitiesBuilder imageInput(Boolean val) { this.imageInput = val; return this; }
                public CapabilitiesBuilder imageOutput(Boolean val) { this.imageOutput = val; return this; }
                public CapabilitiesBuilder tools(Boolean val) { this.tools = val; return this; }
                public CapabilitiesBuilder reasoning(Boolean val) { this.reasoning = val; return this; }

                public Capabilities build() {
                    return new Capabilities(text, imageInput, imageOutput, tools, reasoning);
                }
            }
        }
    }

    public record ProviderCompat(
        Boolean supportsStore,
        Boolean supportsSystemRole,
        Boolean supportsDeveloperRole,
        Boolean requiresAssistantAfterToolResult
    ) {}

    public record TransportDefaults(
        String apiFormat,
        Boolean stream
    ) {}

    public Optional<ModelCard> findModel(String modelId) {
        if (modelId == null) return Optional.empty();
        String lower = modelId.toLowerCase();
        return models.stream()
            .filter(m -> m.id().toLowerCase().equals(lower))
            .findFirst();
    }

    public List<ModelCard> getEnabledModels() {
        return models.stream()
            .filter(m -> m.enabled())
            .toList();
    }
}
