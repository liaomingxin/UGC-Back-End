package com.ugc.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ContentGenerationResponse {
    private String content;
    private ProductDTO product;
} 