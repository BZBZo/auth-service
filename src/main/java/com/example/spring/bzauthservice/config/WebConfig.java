package com.example.spring.bzauthservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 기존 CORS 설정
                registry.addMapping("/auths/token")
                        .allowedOrigins("http://localhost:8084")
                        .allowedMethods("GET")
                        .allowCredentials(true);

                // Swagger UI 및 API Docs 관련 추가 CORS 설정
                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins("http://localhost:8085", "https://localhost:8085")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);

                registry.addMapping("/v3/api-docs/**")
                        .allowedOrigins("http://localhost:8085", "https://localhost:8085")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
