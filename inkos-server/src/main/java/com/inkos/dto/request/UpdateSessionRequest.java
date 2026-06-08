package com.inkos.dto.request;
import lombok.Data;

@Data
public class UpdateSessionRequest {
    private String title;
    private Boolean isDraft;
    private Boolean isStreaming;
}
