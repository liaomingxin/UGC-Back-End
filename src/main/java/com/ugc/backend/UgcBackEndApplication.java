package com.ugc.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.ugc.backend.model")
@EnableJpaRepositories("com.ugc.backend.repository")
public class UgcBackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(UgcBackEndApplication.class, args);
    }
} 