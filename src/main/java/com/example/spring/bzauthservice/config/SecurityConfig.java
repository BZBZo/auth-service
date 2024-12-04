package com.example.spring.bzauthservice.config;


import com.example.spring.bzauthservice.config.filter.JwtAuthFilter;
import com.example.spring.bzauthservice.config.filter.JwtExceptionFilter;
import com.example.spring.bzauthservice.config.oauth.MyAuthenticationFailureHandler;
import com.example.spring.bzauthservice.config.oauth.MyAuthenticationSuccessHandler;
import com.example.spring.bzauthservice.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final MyAuthenticationSuccessHandler successHandler;
    private final MyAuthenticationFailureHandler failureHandler;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/static/**", "/css/**", "/js/**"
                );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/token/**", "/join", "loginSuccess", "/welcome", "/check/**").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(oauth2 -> oauth2   // OAuth2 로그인 설정시작

                        // OAuth2 로그인시 사용자 정보를 가져오는 엔드포인트와 사용자 서비스를 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))

                        // OAuth2 로그인 성공시 처리할 핸들러
                        .successHandler(successHandler)

                        // OAuth2 로그인 실패시 처리할 핸들러
                        .failureHandler(failureHandler));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);

        return http.build();
    }
}

