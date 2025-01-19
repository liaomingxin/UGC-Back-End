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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContentController {
    
    private final AIService aiService;
    private final SelectorConfig selectorConfig;
    
    @PostMapping("/crawl")
    public ResponseEntity<ProductDTO> crawlProduct(@RequestBody ProductDTO productDTO) {
        log.info("Starting product crawl for URL: {}", productDTO.getProductUrl());
        try {
            ProductCrawler crawler = new ProductCrawler(selectorConfig);
            ProductDTO product = crawler.crawlProductData(productDTO.getProductUrl());
            log.info("Successfully crawled product: {}", product.getTitle());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error crawling product: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/generate")
    public ResponseEntity<String> generateContent(@RequestBody ContentGenerationRequest request) {
        log.info("Starting content generation for product: {}", request.getProduct().getTitle());
        try {
            String generatedContent = aiService.generateContent(
                request.getProduct(),
                request.getStyle(),
                request.getLength(),
                request.getLanguage()
            );
            log.info("Successfully generated content with length: {}", generatedContent.length());
            return ResponseEntity.ok(generatedContent);
        } catch (Exception e) {
            log.error("Error generating content: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/generate-mimic")
    public ResponseEntity<ApiResponse<GenerateMimicResponse>> generateMimicContent(
        @RequestBody GenerateMimicRequest request
    ) {
        log.info("Starting mimic content generation with template length: {}", 
            request.getTemplate() != null ? request.getTemplate().length() : 0);
        try {
            // 输入验证
            if (request.getTemplate() == null || request.getTemplate().trim().isEmpty()) {
                log.warn("Invalid request: empty template");
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, "Invalid template", "Template cannot be empty")
                );
            }
            
            if (request.getTemplate().length() < 10) {
                log.warn("Invalid request: template too short");
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(400, "Template too short", "Template must be at least 10 characters")
                );
            }
            
            GenerateMimicResponse response = aiService.generateMimicContent(request);
            log.info("Successfully generated mimic content");
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            log.error("Error generating mimic content: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(
                ApiResponse.error(500, "Internal server error", e.getMessage())
            );
        }
    }
} 