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
            
            // 构建更详细的提示信息
            String prompt = String.format(
                "你是一个专业的电商文案撰写专家。请根据以下要求创作一段商品推荐文案：\n\n" +
                "商品信息：\n" +
                "- 标题：%s\n" +
                "- 价格：%s\n\n" +
                "要求：\n" +
                "1. 文案风格：%s\n" +
                "2. 文案长度：%s\n" +
                "3. 使用语言：%s\n\n" +
                "注意事项：\n" +
                "1. 在文案中自然地插入这个链接标记：[LINK]\n" +
                "2. 在合适的位置插入图片标记：[IMAGE]\n" +
                "3. 根据商品特点和价格优势，突出商品的核心卖点\n" +
                "4. 确保文案语言流畅，富有感染力\n" +
                "5. 适当使用emoji增加文案活力",
                product.getTitle(),
                product.getPrice(),
                getStyleDescription(style),
                getLengthDescription(length),
                getLanguageDescription(language)
            );
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("stream", false);
            requestBody.put("temperature", 0.8); // 增加创造性
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of(
                "role", "system",
                "content", "你是一个专业的电商文案撰写专家，擅长创作富有感染力的营销文案。"
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
            
            // 解析响应并处理标记
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    String content = message.get("content");
                    
                    // 替换链接和图片标记
                    content = content.replace("[LINK]", product.getProductUrl());
                    content = content.replace("[IMAGE]", 
                        String.format("<img src='%s' alt='商品图片' class='content-image'/>", 
                        product.getImageUrl()));
                    
                    return content;
                }
            }
            
            throw new RuntimeException("Failed to generate content: Empty response");
            
        } catch (Exception e) {
            log.error("Error generating content: ", e);
            throw new RuntimeException("Failed to generate content", e);
        }
    }
    
    // 辅助方法：获取风格描述
    private String getStyleDescription(String style) {
        Map<String, String> styleMap = Map.of(
            "professional", "专业正式，突出商品价值和专业特性",
            "casual", "轻松随意，以朋友般的语气介绍商品",
            "humorous", "幽默诙谐，用轻松有趣的方式推荐商品",
            "elegant", "优雅格调，强调商品的品质与格调"
        );
        return styleMap.getOrDefault(style, "专业正式");
    }
    
    // 辅助方法：获取长度描述
    private String getLengthDescription(String length) {
        Map<String, String> lengthMap = Map.of(
            "short", "100字以内的简短文案",
            "medium", "200字左右的适中文案",
            "long", "300字以上的详细文案"
        );
        return lengthMap.getOrDefault(length, "200字左右的适中文案");
    }
    
    // 辅助方法：获取语言描述
    private String getLanguageDescription(String language) {
        Map<String, String> languageMap = Map.of(
            "zh", "中文",
            "en", "英文",
            "jp", "日文"
        );
        return languageMap.getOrDefault(language, "中文");
    }
} 