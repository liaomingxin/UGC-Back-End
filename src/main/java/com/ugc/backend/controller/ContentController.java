package com.ugc.backend.controller;

import com.ugc.backend.dto.*;
import com.ugc.backend.service.AIService;
import com.ugc.backend.config.SelectorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {
    
    private final AIService aiService;
    private final SelectorConfig selectorConfig;
    
    @Autowired
    public ContentController(AIService aiService, SelectorConfig selectorConfig) {
        this.aiService = aiService;
        this.selectorConfig = selectorConfig;
    }
    
    @PostMapping("/crawl")
    public ResponseEntity<ProductDTO> crawlProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductCrawler crawler = new ProductCrawler(selectorConfig);
            ProductDTO product = crawler.crawlProductData(productDTO.getProductUrl());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/generate")
    public ResponseEntity<String> generateContent(@RequestBody ContentGenerationRequest request) {
        try {
            String generatedContent = aiService.generateContent(
                request.getProduct(),
                request.getStyle(),
                request.getLength(),
                request.getLanguage()
            );
            return ResponseEntity.ok(generatedContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/generate-mimic")
    public ResponseEntity<ApiResponse<GenerateMimicResponse>> generateMimicContent(
        @RequestBody GenerateMimicRequest request
    ) {
        try {
            GenerateMimicResponse response = aiService.generateMimicContent(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "Generation failed", e.getMessage()));
        }
    }
} 