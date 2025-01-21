package com.ugc.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableRetry
@ComponentScan(basePackages = "com.ugc.backend")
public class UGCBackEndApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UGCBackEndApplication.class, args);
    }
} 