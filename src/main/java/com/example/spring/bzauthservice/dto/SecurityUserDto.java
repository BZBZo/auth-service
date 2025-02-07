package com.example.spring.bzauthservice.dto;

import com.example.spring.bzauthservice.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecurityUserDto {
    private Long memberNo;
    private String email;
    private String nickname;
    private String phone;
    private String provider;
    private String userRole;
    private String businessNumber;
    private String profilePic;
    private String introduce;

    public Member toMember(){
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .phone(phone)
                .provider(provider)
                .userRole(userRole)
                .businessNumber(businessNumber)
                .introduce(introduce)
                .profilePic(profilePic)
                .build();

    }

}
