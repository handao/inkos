package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotBlank(message = "状态不能为空")
    private String status;
}
