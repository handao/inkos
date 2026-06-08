package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAllowedEmailRequest {
    @NotBlank(message = "邮箱模式不能为空")
    private String emailPattern;
    private String description;
}
