package com.ugc.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class GenerateMimicRequest {
    private String template;
    private String scene;
    private String length;
    private ProductInfo productInfo;

    @Data
    public static class ProductInfo {
        private String title;
        private String description;
        private String price;
        private List<String> features;
    }
} 