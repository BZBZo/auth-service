package com.example.spring.bzauthservice.service;

import com.example.spring.bzauthservice.repository.RefreshTokenRepository;
import com.example.spring.bzauthservice.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void saveTokenInfo(String email, String provider, String refreshToken, String accessToken) {
        repository.save(new RefreshToken(email, provider, accessToken, refreshToken));
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        try {
            String redisKey = "jwtToken:accessToken:" + accessToken;

            Optional<RefreshToken> optionalToken = repository.findByAccessToken(accessToken);
            if (optionalToken.isPresent()) {
                RefreshToken token = optionalToken.get();
                System.out.println("조회된 토큰: " + token);
                repository.delete(token); // DB에서 삭제
                // Redis에서 해당 키 삭제
                redisTemplate.delete(redisKey);
                System.out.println("Redis에서 삭제된 키: " + redisKey);
            } else {
                System.out.println("토큰을 찾을 수 없습니다: " + accessToken);
                throw new IllegalArgumentException("해당 Access Token을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            // 예외가 발생했을 때 로그 출력
            System.out.println("예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw e; // 예외를 다시 던져 트랜잭션 롤백
        }
    }

}
