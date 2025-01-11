package com.ugc.backend.service;

import com.ugc.backend.model.GeneratedContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
public class AIService {
    @Value("${openai.api-key}")
    private String apiKey;
    
    @Value("${openai.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public AIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public String generateContent(String prompt, String style, String length) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("messages", new Object[]{
            Map.of(
                "role", "system",
                "content", "You are a content creator assistant."
            ),
            Map.of(
                "role", "user",
                "content", formatPrompt(prompt, style, length)
            )
        });
        
        try {
            Map<String, Object> response = restTemplate.postForObject(
                baseUrl + "/chat/completions",
                request,
                Map.class
            );
            
            return extractContentFromResponse(response);
        } catch (Exception e) {
            log.error("Error generating content with OpenAI API", e);
            throw new RuntimeException("Failed to generate content", e);
        }
    }
    
    private String formatPrompt(String prompt, String style, String length) {
        return String.format(
            "Please create a %s content in %s style about the following product: %s",
            length, style, prompt
        );
    }
    
    private String extractContentFromResponse(Map<String, Object> response) {
        // 解析OpenAI响应获取生成的内容
        return response.toString(); // 需要根据实际响应格式调整
    }
} 