package com.ugc.backend.dto;

import lombok.Data;

@Data
public class ContentGenerationRequest {
    private String productUrl;
    private String style;
    private String length;
    private String language;
} 