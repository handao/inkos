package com.inkos.dto.request;
import lombok.Data;

@Data
public class UpdateBookRequest {
    private String title;
    private String genre;
    private String status;
    private String outline;
}
