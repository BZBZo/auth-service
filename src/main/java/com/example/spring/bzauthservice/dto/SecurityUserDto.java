package com.example.spring.bzauthservice.dto;

import com.example.spring.bzauthservice.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecurityUserDto {
    private Long memberNo;
    private String email;
    private String role;
    private String nickname;
    private String provider;


    public Member toMember(){
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .userRole(role)
                .provider(provider)
                .build();

    }

}
