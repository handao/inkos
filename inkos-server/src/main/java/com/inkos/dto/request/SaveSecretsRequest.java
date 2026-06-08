package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveSecretsRequest {
    @NotBlank(message = "API Key 不能为空")
    private String apiKey;
}
