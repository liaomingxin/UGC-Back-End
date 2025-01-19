package com.ugc.backend.service.impl;

import com.ugc.backend.dto.ProductDTO;
import com.ugc.backend.dto.GenerateMimicRequest;
import com.ugc.backend.dto.GenerateMimicResponse;
import com.ugc.backend.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

@Service
@Slf4j
@Retryable(
    value = { ResourceAccessException.class },
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000)
)
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

    @Override
    public GenerateMimicResponse generateMimicContent(GenerateMimicRequest request) {
        // 开始生成文案
        log.info("Starting to generate mimic content with request: {}", request);

        try {
            // 设置请求头
            HttpHeaders headers = createHeaders();
            log.debug("Headers set: {}", headers);

            // 构建提示信息
            String prompt = buildMimicPrompt(request);
            log.debug("Prompt built: {}", prompt);

            // 构建请求体
            Map<String, Object> requestBody = createRequestBody(prompt);
            log.debug("Request body created: {}", requestBody);

            // 发送请求并获取响应
            ResponseEntity<Map> response = sendRequest(headers, requestBody);
            log.debug("Response received: {}", response);

            // 解析响应并生成返回结果
            return parseResponse(response);

        } catch (Exception e) {
            // 捕获异常并记录详细日志
            log.error("Error occurred while generating mimic content: ", e);
            throw new RuntimeException("Failed to generate mimic content", e);
        }
    }

    /**
     * 创建请求头，包括内容类型和认证信息。
     *
     * @return HttpHeaders 请求头对象
     */
    private HttpHeaders createHeaders() {
        log.info("Creating headers for the API request");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return headers;
    }

    /**
     * 构建请求体，包含模型、温度、消息等内容。
     *
     * @param prompt 模拟内容的提示
     * @return Map 请求体
     */
    private Map<String, Object> createRequestBody(String prompt) {
        log.info("Creating request body with prompt: {}", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("stream", false);
        requestBody.put("temperature", 0.7);
        requestBody.put("messages", createMessages(prompt));

        log.debug("Request body created: {}", requestBody);
        return requestBody;
    }

    /**
     * 创建消息列表，用于向模型提供输入。
     *
     * @param prompt 模拟内容的提示
     * @return List<Map<String, String>> 消息列表
     */
    private List<Map<String, String>> createMessages(String prompt) {
        log.info("Creating messages for the request with prompt: {}", prompt);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", "你是一个专业的文案创作专家，擅长模仿和创新。请基于用户提供的模板文案，结合场景和要求创作新的文案。"
        ));
        messages.add(Map.of(
                "role", "user",
                "content", prompt
        ));

        log.debug("Messages created: {}", messages);
        return messages;
    }

    /**
     * 发送请求并接收响应。
     *
     * @param headers 请求头
     * @param requestBody 请求体
     * @return ResponseEntity<Map> API响应
     */
    private ResponseEntity<Map> sendRequest(HttpHeaders headers, Map<String, Object> requestBody) {
        log.info("Sending request to the API...");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
        );

        log.info("Request sent, received response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * 解析响应并生成最终的返回对象。
     *
     * @param response API响应
     * @return GenerateMimicResponse 返回生成的文案对象
     * @throws RuntimeException 如果响应无效或不包含有效的内容
     */
    private GenerateMimicResponse parseResponse(ResponseEntity<Map> response) {
        log.info("Parsing response to extract content...");

        if (response.getBody() == null) {
            log.error("Failed to generate mimic content: Empty response");
            throw new RuntimeException("Failed to generate mimic content: Empty response");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        if (choices.isEmpty()) {
            log.error("Failed to generate mimic content: No choices in response");
            throw new RuntimeException("Failed to generate mimic content: No choices in response");
        }

        Map<String, Object> choice = choices.get(0);
        Map<String, String> message = (Map<String, String>) choice.get("message");
        String content = message.get("content");

        log.debug("Content generated: {}", content);

        // 构建响应对象
        return GenerateMimicResponse.builder()
                .content(content)
                .wordCount(countWords(content))
                .sentiment(analyzeSentiment(content))
                .keywords(extractKeywords(content))
                .build();
    }


    private String buildMimicPrompt(GenerateMimicRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请参考以下模板文案，创作一篇新的营销文案：\n\n");
        prompt.append("模板文案：\n").append(request.getTemplate()).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 场景：").append(getSceneDescription(request.getScene())).append("\n");
        prompt.append("2. 文案长度：").append(request.getLength()).append("字左右\n");
        
        if (request.getProductInfo() != null) {
            prompt.append("\n商品信息：\n");
            prompt.append("- 标题：").append(request.getProductInfo().getTitle()).append("\n");
            prompt.append("- 描述：").append(request.getProductInfo().getDescription()).append("\n");
            prompt.append("- 价格：").append(request.getProductInfo().getPrice()).append("\n");
            if (request.getProductInfo().getFeatures() != null && !request.getProductInfo().getFeatures().isEmpty()) {
                prompt.append("- 特点：\n");
                request.getProductInfo().getFeatures().forEach(feature -> 
                    prompt.append("  * ").append(feature).append("\n")
                );
            }
        }
        
        return prompt.toString();
    }
    
    private String getSceneDescription(String scene) {
        switch (scene) {
            case "beauty":
                return "美妆护肤";
            case "fashion":
                return "时尚服饰";
            case "home":
                return "家居生活";
            case "fitness":
                return "健康运动";
            case "travel":
                return "旅游出行";
            case "food":
                return "美食餐饮";
            case "emotion":
                return "情感生活";
            default:
                return "通用场景";
        }
    }
    
    private int countWords(String content) {
        return content.length();
    }
    
    private String analyzeSentiment(String content) {
        // 简单情感分析逻辑，可以根据需要扩展
        if (content.contains("优秀") || content.contains("完美") || content.contains("推荐")) {
            return "positive";
        } else if (content.contains("差") || content.contains("不好") || content.contains("失望")) {
            return "negative";
        }
        return "neutral";
    }
    
    private List<String> extractKeywords(String content) {
        // 简单关键词提取逻辑，可以根据需要扩展
        List<String> keywords = new ArrayList<>();
        // 这里可以实现关键词提取算法
        return keywords;
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