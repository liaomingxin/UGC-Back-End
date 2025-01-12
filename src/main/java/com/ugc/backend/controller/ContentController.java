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
@CrossOrigin(origins = "*") // 允许跨域请求
public class ContentController {
    
    private final AIService aiService;
    private final SelectorConfig selectorConfig;
    
    @PostMapping("/generate")
    public ResponseEntity<ContentGenerationResponse> generateContent(
            @RequestBody ContentGenerationRequest request) {
        try {
            // 创建爬虫实例并获取产品信息
            ProductCrawler crawler = new ProductCrawler(selectorConfig);
            ProductDTO product = crawler.crawlProductData(request.getProductUrl());
            
            // 调用AI服务生成内容
            String generatedContent = aiService.generateContent(
                product,
                request.getStyle(),
                request.getLength(),
                request.getLanguage()
            );
            
            // 返回生成的内容和产品信息
            return ResponseEntity.ok(new ContentGenerationResponse(generatedContent));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 