package com.ugc.backend.controller;

import com.ugc.backend.model.Product;
import com.ugc.backend.model.GeneratedContent;
import com.ugc.backend.service.CrawlerService;
import com.ugc.backend.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content")
@Slf4j
public class ContentGenerationController {
    
    @Autowired
    private CrawlerService crawlerService;
    
    @Autowired
    private AIService aiService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateContent(@RequestBody ContentGenerationRequest request) {
        try {
            // 1. 爬取产品数据
            Product product = crawlerService.crawlProductData(request.getProductUrl());
            
            // 2. 使用AI生成内容
            String generatedContent = aiService.generateContent(
                product.getDescription(),
                request.getStyle(),
                request.getLength()
            );
            
            return ResponseEntity.ok(new ContentGenerationResponse(generatedContent));
        } catch (Exception e) {
            log.error("Error generating content", e);
            return ResponseEntity.status(500)
                .body(new ErrorResponse("Failed to generate content"));
        }
    }
} 