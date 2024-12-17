package com.example.spring.bzauthservice.service;

import com.example.spring.bzauthservice.config.jwt.JwtProperties;
import com.example.spring.bzauthservice.dto.ValidTokenResponseDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class TokenProviderService {

    private final JwtProperties jwtProperties;

    public ValidTokenResponseDTO validToken(String token) {
        int result = validTokenStatus(token);
        return ValidTokenResponseDTO.builder()
                .statusNum(result)
                .build();
    }

    public int validTokenStatus(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey( getSecretKey() ) // SecretKey 객체 사용
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return 1;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            System.out.println("Token이 만료되었습니다.");
            return 2;
        } catch (Exception e) {
            // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            System.out.println("err : " + e.getMessage());
            return 3;
        }
    }

    private SecretKey getSecretKey() {
        String secret = jwtProperties.getSecret();
        return Keys.hmacShaKeyFor(secret.getBytes());  // Base64 디코딩하지 않고 그대로 사용
    }

}
