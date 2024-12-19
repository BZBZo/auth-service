package com.example.spring.bzauthservice.controller;

import com.example.spring.bzauthservice.config.jwt.JwtUtil;
import com.example.spring.bzauthservice.dto.JoinResponseDTO;
import com.example.spring.bzauthservice.dto.DuplicateResponseDTO;
import com.example.spring.bzauthservice.dto.SecurityUserDto;
import com.example.spring.bzauthservice.entity.Member;
import com.example.spring.bzauthservice.repository.RefreshTokenRepository;
import com.example.spring.bzauthservice.service.MemberService;
import com.example.spring.bzauthservice.token.RefreshToken;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
public class SignApiController {

    private final MemberService memberService;
    private final RefreshTokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(SignApiController.class);

    @PostMapping("/join")
    public ResponseEntity<JoinResponseDTO> join(@RequestBody SecurityUserDto securityUserDto) {
        log.info("join");
        try{
            memberService.join(securityUserDto.toMember());
            return ResponseEntity.ok(
                    JoinResponseDTO.builder()
                            .url("/webs/signin")
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    JoinResponseDTO.builder()
                            .url("/webs/join")
                            .build()
            );
        }
    }

    @PostMapping("/check/businessNumber")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> checkBusinessNumber(@Valid @RequestParam String businessNumber) {
        System.out.println("사업자 번호 중복 체크");
        boolean exists = memberService.checkBusinessNumberExists(businessNumber);
        System.out.println(exists);
        if (exists) {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("이미 가입한 판매자입니다.")
                            .status("disable")
                            .build()
            );
        } else {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("등록 가능한 사업자번호입니다.")
                            .status("available")
                            .build()
            );
        }
    }

    @PostMapping("/check/nickname")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> checkNickname(@Valid @RequestParam String nickname) {
        boolean exists = memberService.checkNicknameExists(nickname);
        System.out.println(nickname);
        System.out.println(exists);
        if (exists) {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("동일한 이름이 존재합니다. 다른 이름을 입력해주세요")
                            .status("disable")
                            .build()
            );
        } else {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("사용 가능합니다.")
                            .status("available")
                            .build()
            );
        }
    }

    @PostMapping("/check/sellerPhone")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> checkSellerPhone(@Valid @RequestParam String sellerPhone) {
        boolean exists = memberService.checkSellerPhoneExists(sellerPhone);
        if (exists) {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("이미 가입한 판매자입니다.")
                            .status("disable")
                            .build()
            );
        } else {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("사용 가능한 번호입니다.")
                            .status("available")
                            .build()
            );
        }
    }

    @PostMapping("/check/customerPhone")
    @ResponseStatus(HttpStatus.OK)
    //@Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다.")
    public ResponseEntity<?> checkCustomerPhone(@Valid @RequestParam String customerPhone) {
        boolean exists = memberService.checkCustomerPhoneExists(customerPhone);
        if (exists) {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("이미 가입한 구매자입니다.")
                            .status("disable")
                            .build()
            );
        } else {
            return ResponseEntity.ok(
                    DuplicateResponseDTO.builder()
                            .message("사용 가능한 번호입니다.")
                            .status("available")
                            .build()
            );
        }
    }

    @GetMapping("/user/info")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> loadUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        // Bearer 부분을 제거
        String token = authorizationHeader.replace("Bearer ", "");

        // JWT 토큰 검증 로그
        logger.info("Extracted Token: {}", token);

        // 액세스 토큰으로 Refresh 토큰 객체를 조회
        Optional<RefreshToken> refreshToken = tokenRepository.findByAccessToken(token);

        if (refreshToken.isPresent()) {
            // RefreshToken이 존재하는 경우, 토큰 검증
            logger.info("RefreshToken found for token: {}", token);

            boolean isTokenValid = jwtUtil.verifyToken(refreshToken.get().getRefreshToken());
            logger.info("JWT Token validity: {}", isTokenValid);

            if (isTokenValid) {
                String email = refreshToken.get().getId();
                String provider = refreshToken.get().getProvider();
                Optional<Member> findMember = memberService.findByEmailAndProvider(email, provider);

                if (findMember.isPresent()) {
                    Member member = findMember.get();

                    // Member 정보를 SecurityUserDto로 변환
                    SecurityUserDto securityUserDto = SecurityUserDto.builder()
                            .memberNo(member.getMemberNo())
                            .email(member.getEmail())
                            .nickname(member.getNickname())
                            .phone(member.getPhone())
                            .provider(member.getProvider())
                            .introduce(member.getIntroduce())
                            .role(member.getUserRole())
                            .businessNumber(member.getBusinessNumber())
                            .build();

                    // 로그: 회원 정보 반환
                    logger.info("Returning user info for member: {}", member.getNickname());
                    return ResponseEntity.ok(securityUserDto);
                } else {
                    // 회원 정보가 없을 경우
                    logger.error("User not found for email: {}", email);
                    logger.error("User not found for provider: {}", provider);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다.");
                }
            } else {
                // 토큰이 유효하지 않음
                logger.error("Invalid refresh token for token: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }
        } else {
            // RefreshToken이 존재하지 않음
            logger.error("RefreshToken not found for token: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }
}