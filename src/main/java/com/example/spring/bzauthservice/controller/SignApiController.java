package com.example.spring.bzauthservice.controller;

import com.example.spring.bzauthservice.dto.JoinResponseDTO;
import com.example.spring.bzauthservice.dto.DuplicateResponseDTO;
import com.example.spring.bzauthservice.dto.SecurityUserDto;
import com.example.spring.bzauthservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
public class SignApiController {

    private final MemberService memberService;

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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> checkBusinessNumber(@Valid @RequestBody String businessNumber) {
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> checkNickname(@Valid @RequestBody String nickname) {
        boolean exists = memberService.checkNicknameExists(nickname);
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> checkSellerPhone(@Valid @RequestBody String phone) {
        boolean exists = memberService.checkSellerPhoneExists(phone);
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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> checkCustomerPhone(@Valid @RequestBody String phone) {
        boolean exists = memberService.checkCustomerPhoneExists(phone);
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


}
