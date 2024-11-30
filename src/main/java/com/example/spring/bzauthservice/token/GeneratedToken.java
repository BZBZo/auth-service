package com.example.spring.bzauthservice.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneratedToken {
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    // refreshToken에 대한 Getter도 포함시킬 수 있습니다.
    public String getRefreshToken() {
        return refreshToken;
    }
}