package com.example.spring.bzauthservice.config.filter;

import com.example.spring.bzauthservice.config.jwt.JwtUtil;
import com.example.spring.bzauthservice.dto.SecurityUserDto;
import com.example.spring.bzauthservice.entity.Member;
import com.example.spring.bzauthservice.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request Header에서 AccessToken을 가져온다.
        String atc = request.getHeader("Authorization");

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과)
        if (!StringUtils.hasText(atc)) {
            doFilter(request, response, filterChain);
            return;
        }

        // AccessToken을 검증하고, 만료되었을경우 예외를 발생시킨다.
        if (!jwtUtil.verifyToken(atc)) {
            throw new JwtException("Access Token 만료!");
        }

        // AccessToken의 값이 있고, 유효한 경우에 진행한다.
        if (jwtUtil.verifyToken(atc)) {

            // AccessToken 내부의 payload에서 email과 provider 정보를 추출한다.
            String email = jwtUtil.getUid(atc); // 기존에 email을 추출하는 메서드
            String provider = jwtUtil.getProvider(atc); // 새롭게 추가된 provider 추출 메서드

            // email과 provider로 user를 조회한다. 없다면 예외를 발생시킨다.
            Member findMember = memberRepository.findByEmailAndProvider(email, provider)
                    .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

            // SecurityContext에 등록할 User 객체를 만들어준다.
            SecurityUserDto userDto = SecurityUserDto.builder()
                    .memberNo(findMember.getMemberNo())
                    .email(findMember.getEmail())
                    .provider(findMember.getProvider())
                    .role(findMember.getUserRole()) //ROLE_
                    .nickname(findMember.getNickname())
                    .build();

            // SecurityContext에 인증 객체를 등록해준다.
            Authentication auth = getAuthentication(userDto);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }

}
