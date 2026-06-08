package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class SaveLlmServiceRequest {
    @NotBlank(message = "服务类型不能为空")
    private String serviceType;
    private String label;
    private String baseUrl;
    private String apiType;
    private List<String> models;
    private String defaultModel;
    private boolean isCoverProvider;
}
