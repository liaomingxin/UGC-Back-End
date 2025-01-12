package com.ugc.backend.service.impl;

import com.ugc.backend.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;

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
        if (response != null && response.containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                return (String) message.get("content");
            }
        }
        throw new RuntimeException("Invalid response format from OpenAI API");
    }
} 