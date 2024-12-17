package com.example.spring.bzauthservice.dto;

import lombok.Getter;

@Getter
public class TokenRefreshRequestDTO {
    private String refreshToken;
    private String accessToken;
}
