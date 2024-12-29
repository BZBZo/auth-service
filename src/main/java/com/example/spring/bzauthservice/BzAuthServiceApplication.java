package com.example.spring.bzauthservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BzAuthServiceApplication {
    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Set system properties for AWS keys
        System.setProperty("AWS_ACCESS_KEY_ID", dotenv.get("AWS_ACCESS_KEY_ID"));
        System.setProperty("AWS_SECRET_ACCESS_KEY", dotenv.get("AWS_SECRET_ACCESS_KEY"));

        // Set system properties for Google OAuth
        System.setProperty("GOOGLE_ID", dotenv.get("GOOGLE_ID"));
        System.setProperty("GOOGLE_SECRET", dotenv.get("GOOGLE_SECRET"));

        // Set system properties for Kakao OAuth
        System.setProperty("KAKAO_ID", dotenv.get("KAKAO_ID"));

        // Set system properties for Naver OAuth
        System.setProperty("NAVER_ID", dotenv.get("NAVER_ID"));
        System.setProperty("NAVER_SECRET", dotenv.get("NAVER_SECRET"));

        SpringApplication.run(BzAuthServiceApplication.class, args);
    }
}
