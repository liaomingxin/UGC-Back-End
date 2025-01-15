package com.ugc.backend.controller;

import com.ugc.backend.dto.ContentGenerationRequest;
import com.ugc.backend.dto.ContentGenerationResponse;
import com.ugc.backend.dto.ProductDTO;
import com.ugc.backend.service.AIService;
import com.ugc.backend.dto.ProductCrawler;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.ugc.backend.config.SelectorConfig;
import com.ugc.backend.dto.GenerateMimicRequest;
import com.ugc.backend.dto.GenerateMimicResponse;
import com.ugc.backend.dto.ApiResponse;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContentController {
    
    private final AIService aiService;
    private final SelectorConfig selectorConfig;
    
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
            // 输入验证
            if (request.getTemplate() == null || request.getTemplate().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, "Invalid template", "Template cannot be empty")
                );
            }
            
            if (request.getTemplate().length() < 10) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, "Template too short", "Template must be at least 10 characters")
                );
            }
            
            GenerateMimicResponse response = aiService.generateMimicContent(request);
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                ApiResponse.error(500, "Internal server error", e.getMessage())
            );
        }
    }
} 