package com.example.spring.bzauthservice.config.oauth;

import com.example.spring.bzauthservice.config.jwt.JwtUtil;
import com.example.spring.bzauthservice.token.GeneratedToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("성공 핸들러 "+oAuth2User.toString());

        // 사용자 이메일을 가져온다.
        String email = oAuth2User.getAttribute("email");
        // 서비스 제공 플랫폼(GOOGLE, KAKAO, NAVER)이 어디인지 가져온다.
        String provider = oAuth2User.getAttribute("provider");
        // 사용자 닉네임을 가져온다.
        String nickname = oAuth2User.getAttribute("nickname");

        // CustomOAuth2UserService에서 셋팅한 로그인한 회원 존재 여부를 가져온다.
        boolean isExist = oAuth2User.getAttribute("exist");
        // OAuth2User로 부터 Role을 얻어온다.
        String role = oAuth2User.getAuthorities().stream().
                findFirst() // 첫번째 Role을 찾아온다.
                .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                .getAuthority(); // Role을 가져온다.

        // 회원이 존재할경우
        if (isExist) {
            // 회원이 존재하면 jwt token 발행을 시작한다.
            GeneratedToken token = jwtUtil.generateToken(email, provider, nickname, role);
            log.info("jwtToken = {}", token.getAccessToken());

            // Bearer 접두어 없이 순수한 JWT 토큰을 설정
            String jwtToken = token.getAccessToken();

            // HTTP-Only 쿠키 생성
            Cookie jwtCookie = new Cookie("Authorization", jwtToken); // Bearer 제거
            jwtCookie.setHttpOnly(true); // HTTP-Only 설정
            jwtCookie.setSecure(true); // HTTPS에서만 전송되도록 설정
            jwtCookie.setPath("/"); // 쿠키를 전체 도메인에서 사용할 수 있도록 설정
            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효기간 (7일)

            // 응답에 쿠키 추가
            response.addCookie(jwtCookie);

            // 리다이렉트 처리
            String targetUrl = "http://localhost:8084/webs/loginSuccess";
            log.info("redirect 준비");
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } else {
            // 회원 존재하지 않으면 여기로
            // 회원이 존재하지 않을경우, 서비스 제공자와 email을 쿼리스트링으로 전달하는 url을 만들어준다.
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8084/webs/join")
                    .queryParam("email", (String) oAuth2User.getAttribute("email"))
                    .queryParam("provider", provider)
                    .queryParam("role", role)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            log.info(targetUrl);
            // 회원가입 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

}