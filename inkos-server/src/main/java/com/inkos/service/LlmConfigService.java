package com.inkos.service;

import com.inkos.dto.request.SaveLlmServiceRequest;
import com.inkos.dto.response.LlmServiceResponse;
import com.inkos.entity.LlmServiceConfig;
import com.inkos.entity.Secret;
import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;
import com.inkos.repository.LlmServiceConfigRepository;
import com.inkos.repository.SecretRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmConfigService {
    private final LlmServiceConfigRepository configRepository;
    private final SecretRepository secretRepository;

    public List<LlmServiceResponse> listServices(Long userId) {
        return configRepository.findByUserId(userId).stream()
                .map(LlmServiceResponse::from).toList();
    }

    @Transactional
    public LlmServiceResponse saveService(SaveLlmServiceRequest request, Long userId) {
        LlmServiceConfig config = LlmServiceConfig.builder()
                .userId(userId)
                .serviceType(request.getServiceType())
                .label(request.getLabel())
                .baseUrl(request.getBaseUrl())
                .apiType(request.getApiType())
                .models(request.getModels() != null ? String.join(",", request.getModels()) : null)
                .defaultModel(request.getDefaultModel())
                .isCoverProvider(request.isCoverProvider())
                .build();
        return LlmServiceResponse.from(configRepository.save(config));
    }

    @Transactional
    public void deleteService(Long id, Long userId) {
        LlmServiceConfig config = configRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!config.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        configRepository.delete(config);
    }

    @Transactional
    public void saveSecret(String serviceKey, String apiKey, Long userId) {
        Secret secret = Secret.builder()
                .userId(userId)
                .serviceKey(serviceKey)
                .encryptedKey(apiKey)
                .build();
        secretRepository.save(secret);
    }

    public boolean hasSecret(String serviceKey, Long userId) {
        return secretRepository.findByUserIdAndServiceKey(userId, serviceKey).isPresent();
    }
}
