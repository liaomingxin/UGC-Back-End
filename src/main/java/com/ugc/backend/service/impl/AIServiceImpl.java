package com.ugc.backend.service.impl;

import com.ugc.backend.dto.ProductDTO;
import com.ugc.backend.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
public class AIServiceImpl implements AIService {
    
    @Value("${openai.api-key}")
    private String apiKey;
    
    @Value("${openai.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public AIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String generateContent(ProductDTO product, String style, String length, String language) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 构建提示信息
            String prompt = String.format(
                "Please create a %s %s marketing content in %s language for the following product:\n" +
                "Product Title: %s\n" +
                "Price: %s\n" +
                "Product URL: %s\n" +
                "Image URL: %s",
                length, style, language,
                product.getTitle(),
                product.getPrice(),
                product.getProductUrl(),
                product.getImageUrl()
            );
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("stream", false);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of(
                "role", "system",
                "content", "You are a professional marketing content creator."
            ));
            messages.add(Map.of(
                "role", "user",
                "content", prompt
            ));
            
            requestBody.put("messages", messages);
            
            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            // 解析响应
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    return message.get("content");
                }
            }
            
            throw new RuntimeException("Failed to generate content: Empty response");
            
        } catch (Exception e) {
            log.error("Error generating content: ", e);
            throw new RuntimeException("Failed to generate content", e);
        }
    }
} 