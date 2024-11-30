package com.example.spring.bzauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseStatus {
    private int status;
    private String token;

    public static TokenResponseStatus addStatus(int status, String token) {
        return new TokenResponseStatus(status, token);
    }
}
