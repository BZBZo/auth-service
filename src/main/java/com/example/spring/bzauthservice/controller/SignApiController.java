package com.example.spring.bzauthservice.controller;

import com.example.spring.bzauthservice.dto.JoinResponseDTO;
import com.example.spring.bzauthservice.dto.SecurityUserDto;
import com.example.spring.bzauthservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class SignApiController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<JoinResponseDTO> join(@RequestBody SecurityUserDto securityUserDto) {
        try{
            memberService.join(securityUserDto.toMember());
            return ResponseEntity.ok(
                    JoinResponseDTO.builder()
                            .url("/welcome")
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    JoinResponseDTO.builder()
                            .url("/welcome")
                            .build()
            );
        }
    }
    @GetMapping("/welcome")
    public String login() {
        return "login";
    }
}
