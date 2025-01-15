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
} 