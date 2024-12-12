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
import org.springframework.web.cors.*;

import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auths/signin/**", "/auths/join", "/auths/loginFailure", "/auths/check/**","/auths/token").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(oauth2 -> oauth2   // OAuth2 로그인 설정 시작
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8084")); // front-service의 주소
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/auths/**", configuration); // /auth/** 경로에 CORS 설정 적용
        return source;
    }
}
