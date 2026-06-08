package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateChapterRequest {
    @NotNull(message = "章节号不能为空")
    private Integer chapterNumber;
    @NotBlank(message = "章节标题不能为空")
    private String title;
    private String content;
}
