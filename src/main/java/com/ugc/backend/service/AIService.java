package com.ugc.backend.service;

import com.ugc.backend.dto.ProductDTO;
import com.ugc.backend.dto.GenerateMimicRequest;
import com.ugc.backend.dto.GenerateMimicResponse;

public interface AIService {
    String generateContent(ProductDTO product, String style, String length, String language);
    
    GenerateMimicResponse generateMimicContent(GenerateMimicRequest request);
} 