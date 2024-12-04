package com.example.spring.bzauthservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DuplicateResponseDTO {
    private String message;
    private String status;
}
