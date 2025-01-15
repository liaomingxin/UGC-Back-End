package com.ugc.backend.dto;

import lombok.Data;

@Data
public class ContentGenerationRequest {
    private ProductDTO product;
    private String style;
    private String length;
    private String language;
} 