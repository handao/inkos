package com.inkos.dto.request;
import lombok.Data;

@Data
public class CreateSessionRequest {
    private String bookId;
    private String title;
    private String mode;
}
