package com.example.spring.bzauthservice.controller;

import com.example.spring.bzauthservice.config.jwt.JwtUtil;
import com.example.spring.bzauthservice.dto.*;
import com.example.spring.bzauthservice.repository.RefreshTokenRepository;
import com.example.spring.bzauthservice.service.RefreshTokenService;
import com.example.spring.bzauthservice.service.TokenProviderService;
import com.example.spring.bzauthservice.swagger.AuthControllerDocs;
import com.example.spring.bzauthservice.token.GeneratedToken;
import com.example.spring.bzauthservice.token.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auths/token")
public class AuthController implements AuthControllerDocs {

    private final RefreshTokenRepository tokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenProviderService tokenProviderService;
    private final JwtUtil jwtUtil;

//    @GetMapping
//    public ResponseEntity<?> getToken(HttpServletRequest request) {
//        //http servlet request 쿠키에서 refresh token 꺼내옴
//        //근데 accessToken이지 않나...?
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("Authorization".equals(cookie.getName())) {
//                    String token = cookie.getValue();
//                    Optional<RefreshToken> refreshToken = tokenRepository.findByAccessToken(token);
//                    String strRefreshToken = refreshToken.get().getRefreshToken();
//                    // 쿠키에서 꺼내온 refresh token을 검증함
//                    if (jwtUtil.verifyToken(strRefreshToken)) {
//                        return ResponseEntity.ok(Collections.singletonMap("token", token));
//                    }
//                }
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }

    @PostMapping("/logout")
    public ResponseEntity<StatusResponseDto> logout(@RequestHeader("Authorization") final String accessToken) {

        // 엑세스 토큰으로 현재 Redis 정보 삭제
        refreshTokenService.removeRefreshToken(accessToken);
        return ResponseEntity.ok(StatusResponseDto.addStatus(200));
    }

    @PostMapping("/validToken")
    public ResponseEntity<?> validToken(@RequestBody ValidTokenRequestDTO tokenRequest) {
        // 요청 본문에서 token을 받아서 처리
        String token = tokenRequest.getToken();

        // 서비스에서 토큰 검증 처리
        ValidTokenResponseDTO response = tokenProviderService.validToken(token);

        return ResponseEntity.ok(response);
    }

    //쌤 코드 + 내 코드 짬뽕
    @PostMapping("/refresh")
    public RefreshTokenClientResponseDTO refreshToken(@RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO) {
        log.info("refresh");

        String accessToken = tokenRefreshRequestDTO.getAccessToken();
        System.out.println("accessToken :: " + accessToken);

        Optional<RefreshToken> refreshToken = tokenRepository.findByAccessToken(accessToken);
        String strRefreshToken = String.valueOf(refreshToken.get().getRefreshToken());

        System.out.println(strRefreshToken+"리프레시 찾아옴");

        int result = tokenProviderService.validToken(strRefreshToken).getStatusNum();

        String newAccessToken = null;
//        String newRefreshToken = null;

        if (result == 1) {
            RefreshToken resultToken = refreshToken.get();

            String email = jwtUtil.getUid(strRefreshToken);
            String provider = jwtUtil.getProvider(strRefreshToken);
            String role = jwtUtil.getRole(strRefreshToken);

//            GeneratedToken newToken = jwtUtil.generateToken(email, provider, role);
            newAccessToken = jwtUtil.generateAccessToken(email, provider, role);

            resultToken.updateAccessToken(newAccessToken);
            tokenRepository.save(resultToken);

        }
        RefreshTokenClientResponseDTO responseDTO = RefreshTokenClientResponseDTO.builder()
                .status(result)
                .accessToken(newAccessToken)
//                .refreshToken(newRefreshToken)
                .build();

        return responseDTO;
    }

    // 내 코드
//    @PostMapping("/refresh")
//    public ResponseEntity<TokenResponseStatus> refresh(@RequestHeader("Authorization") final String accessToken) {
//
//        // 액세스 토큰으로 Refresh 토큰 객체를 조회
//        Optional<RefreshToken> refreshToken = tokenRepository.findByAccessToken(accessToken);
//
//        // RefreshToken이 존재하고 유효하다면 실행
//        if (refreshToken.isPresent() && jwtUtil.verifyToken(refreshToken.get().getRefreshToken())) {
//            // RefreshToken 객체를 꺼내온다.
//            RefreshToken resultToken = refreshToken.get();
//            // 권한과 아이디를 추출해 새로운 액세스토큰을 만든다.
//            String newAccessToken = jwtUtil.generateAccessToken(resultToken.getId(), jwtUtil.getProvider(resultToken.getRefreshToken()), jwtUtil.getRole(resultToken.getRefreshToken()));
//            // 액세스 토큰의 값을 수정해준다.
//            resultToken.updateAccessToken(newAccessToken);
//            tokenRepository.save(resultToken);
//            // 새로운 액세스 토큰을 반환해준다.
//            return ResponseEntity.ok(TokenResponseStatus.addStatus(200, newAccessToken));
//        }
//
//        return ResponseEntity.badRequest().body(TokenResponseStatus.addStatus(400, null));
//    }

}
