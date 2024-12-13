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
        if (StringUtils.hasText(atc) && atc.startsWith("Bearer ")) {
            atc = atc.substring(7);
        }

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과)
        if (!StringUtils.hasText(atc)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.verifyToken(atc)) {
                // AccessToken 내부의 payload에서 email과 provider 정보를 추출한다.
                String email = jwtUtil.getUid(atc);
                String provider = jwtUtil.getProvider(atc);

                // email과 provider로 user를 조회한다. 없다면 예외를 발생시킨다.
                Member findMember = memberRepository.findByEmailAndProvider(email, provider)
                        .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

                // SecurityContext에 등록할 User 객체를 만들어준다.
                SecurityUserDto userDto = SecurityUserDto.builder()
                        .memberNo(findMember.getMemberNo())
                        .email(findMember.getEmail())
                        .nickname(findMember.getNickname())
                        .phone(findMember.getPhone())
                        .provider(findMember.getProvider())
                        .introduce(findMember.getIntroduce())
                        .role(findMember.getUserRole())
                        .businessNumber(findMember.getBusinessNumber())
                        .build();

                // SecurityContext에 인증 객체를 등록해준다.
                Authentication auth = getAuthentication(userDto);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException e) {
            logger.error("JWT token validation failed: {}");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }


    public Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }

}
