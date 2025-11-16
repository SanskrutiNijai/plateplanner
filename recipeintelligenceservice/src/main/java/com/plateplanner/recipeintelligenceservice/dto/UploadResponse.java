package com.plateplanner.recipeintelligenceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    private String fileId;
    private String filename;
    private String contentType;
    private long size;
    private String viewUrl;
    private String downloadUrl;
}

