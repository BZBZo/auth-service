package com.example.spring.bzauthservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshTokenClientResponseDTO {
    private int status;
    private String accessToken;
    private String refreshToken;
}
