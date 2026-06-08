package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBookRequest {
    @NotBlank(message = "书名不能为空")
    private String title;
    private String genre;
    private String language;
}
