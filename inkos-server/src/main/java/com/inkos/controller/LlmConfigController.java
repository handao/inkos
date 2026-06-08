package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.SaveLlmServiceRequest;
import com.inkos.dto.request.SaveSecretsRequest;
import com.inkos.dto.response.LlmServiceResponse;
import com.inkos.security.UserContext;
import com.inkos.service.LlmConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LlmConfigController {
    private final LlmConfigService llmConfigService;

    @GetMapping("/services")
    public ApiResponse<List<LlmServiceResponse>> listServices(
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(llmConfigService.listServices(user.getUserId()));
    }

    @PostMapping("/services")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LlmServiceResponse> saveService(
            @Valid @RequestBody SaveLlmServiceRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(llmConfigService.saveService(request, user.getUserId()));
    }

    @DeleteMapping("/services/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserContext user) {
        llmConfigService.deleteService(id, user.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/secrets/{serviceKey}")
    public ApiResponse<Map<String, Boolean>> checkSecret(
            @PathVariable String serviceKey,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(Map.of(
                "hasKey", llmConfigService.hasSecret(serviceKey, user.getUserId())));
    }

    @PutMapping("/secrets/{serviceKey}")
    public ApiResponse<Void> saveSecret(
            @PathVariable String serviceKey,
            @Valid @RequestBody SaveSecretsRequest request,
            @AuthenticationPrincipal UserContext user) {
        llmConfigService.saveSecret(serviceKey, request.getApiKey(), user.getUserId());
        return ApiResponse.success();
    }
}
