package com.example.spring.bzauthservice.dto;

import com.example.spring.bzauthservice.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberResponseDTO {
    private Long memberNo;
    private String email;
    private String phone;
    private String userRole;
    private String provider;
    private String businessNumber;
    private String profilePic;
    private String introduce;

    public Member toMember(Long memberNo, String email, String phone, String userRole, String provider, String businessNumber, String profilePic, String introduce){
        return Member.builder()
                .memberNo(memberNo)
                .email(email)
                .phone(phone)
                .userRole(userRole)
                .provider(provider)
                .businessNumber(businessNumber)
                .profilePic(profilePic)
                .introduce(introduce)
                .build();
    }

}
