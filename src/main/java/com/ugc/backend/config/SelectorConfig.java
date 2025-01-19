package com.ugc.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class SelectorConfig {
    private Map<String, SiteConfig> selectors;

    @Data
    public static class SiteConfig {
        private List<String> domain;
        private String title;
        private PriceConfig price;
        private String image;
    }

    @Data
    public static class PriceConfig {
        private String className;
        private String pattern;
    }
} 