package com.ugc.backend.service;

import com.ugc.backend.dto.ProductDTO;

public interface AIService {
    String generateContent(ProductDTO product, String style, String length, String language);
} 