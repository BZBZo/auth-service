package com.example.spring.bzauthservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2/authorization")
public class OAuth2AuthorizationController {

    @GetMapping("/{provider}")
    public void authorize(@PathVariable String provider) {
        // 로깅, 디버깅 또는 추가 작업
        System.out.println("OAuth2 authorization initiated for provider: " + provider);
    }
}