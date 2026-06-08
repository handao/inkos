package com.inkos.dto.response;
import com.inkos.entity.LlmServiceConfig;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class LlmServiceResponse {
    private final Long id;
    private final String serviceType;
    private final String label;
    private final String baseUrl;
    private final String apiType;
    private final String models;
    private final String defaultModel;
    private final boolean isCoverProvider;
    private final boolean isDefault;

    public static LlmServiceResponse from(LlmServiceConfig config) {
        return LlmServiceResponse.builder()
                .id(config.getId()).serviceType(config.getServiceType())
                .label(config.getLabel()).baseUrl(config.getBaseUrl())
                .apiType(config.getApiType()).models(config.getModels())
                .defaultModel(config.getDefaultModel())
                .isCoverProvider(config.getIsCoverProvider() != null && config.getIsCoverProvider())
                .isDefault(config.getIsDefault() != null && config.getIsDefault())
                .build();
    }
}
