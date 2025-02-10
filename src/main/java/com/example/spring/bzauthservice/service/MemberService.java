package com.example.spring.bzauthservice.service;

import com.example.spring.bzauthservice.dto.SecurityUserDto;
import com.example.spring.bzauthservice.entity.Member;
import com.example.spring.bzauthservice.repository.MemberRepository;
import com.example.spring.bzauthservice.repository.RefreshTokenRepository;
import com.example.spring.bzauthservice.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private Logger logger;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

//    public void join(Member member) {
//        System.out.println(member.getEmail()+"  "+member.getNickname()+"  "+member.getUserRole());
//        memberRepository.save(member);
//        System.out.println("저장했어요");
//    }

    public void join(Member member) {
        System.out.println(member.getEmail()+"  "+member.getNickname()+"  "+member.getUserRole());
        log.info("회원가입 요청: email={}, nickname={}", member.getEmail(), member.getNickname());
        log.info("현재 profilePic 값: {}", member.getProfilePic());

        // 기본 프로필 이미지 적용 여부 확인
        if (member.getProfilePic() == null || member.getProfilePic().isEmpty()) {
            member.setProfilePic(defaultProfileImageUrl);
            log.info("기본 프로필 이미지 적용: {}", defaultProfileImageUrl);
        }

        memberRepository.save(member);
        System.out.println("저장했어요");
        log.info("회원가입 완료: memberNo={}, profilePic={}", member.getMemberNo(), member.getProfilePic());
    }

    public boolean checkBusinessNumberExists(String businessNumber) {
        return memberRepository.findByBusinessNumber(businessNumber).isPresent();
    }

    public boolean checkNicknameExists(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    public boolean checkSellerPhoneExists(String phone) {
        return memberRepository.findByPhoneAndUserRole(phone, "ROLE_SELLER").isPresent();
    }

    public boolean checkCustomerPhoneExists(String phone) {
        return memberRepository.findByPhoneAndUserRole(phone, "ROLE_CUSTOMER").isPresent();
    }

    public Optional<Member> findByEmailAndProvider(String email, String provider) {
        return memberRepository.findByEmailAndProvider(email, provider);
    }

    public Optional<Member> findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }



    // 회원 삭제
    public boolean deleteMember(Long memberNo) {
        try {
            // 해당 사용자의 RefreshToken을 먼저 삭제
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccessToken(memberNo.toString());
            refreshToken.ifPresent(token -> refreshTokenRepository.delete(token));

            // Member 삭제
            memberRepository.deleteById(memberNo);

            return true;
        } catch (Exception e) {
            logger.error("회원 탈퇴 중 오류 발생: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Member updateMemberField(Member member, String field, String value) {
        switch (field) {
            case "introduce":
            case "shopIntroduce":
                member.setIntroduce(value);  // introduce 또는 shopIntroduction 필드 처리
                break;
            case "profileImage":
                member.setProfilePic(value);
            case "shopImage":
                member.setProfilePic(value);  // profileImage 또는 shopImage 필드 처리
                break;
            case "nickname":
            case "shopName":
                member.setNickname(value);  // nickname 또는 shopName 필드 처리
                break;
            case "phone":
            case "shopPhone":
                member.setPhone(value);  // phone 또는 shopPhone 필드 처리
                break;
            // 다른 필드들에 대한 처리 추가 (필요시)
            default:
                throw new IllegalArgumentException("Unsupported field: " + field);  // 기본값 처리
        }
        return memberRepository.save(member);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> findByMemberNo(Long memberNo) {
        return memberRepository.findById(memberNo);
    }

    public List<SecurityUserDto> fetchWritersByMemberNos(Set<Long> memberNos) {
        // memberNos에 해당하는 회원 정보를 조회
        List<Member> members = memberRepository.findByMemberNoIn(memberNos);

        // 조회한 회원 정보를 SecurityUserDto로 변환하여 반환
        return members.stream()
                .map(member -> SecurityUserDto.builder()
                        .memberNo(member.getMemberNo())
                        .nickname(member.getNickname())
                        .profilePic(member.getProfilePic())
                        .introduce(member.getIntroduce())
                        .build())
                .collect(Collectors.toList());
    }


    public Member updateMemberImage(Member member, String field, MultipartFile file) {
        String imageUrl = uploadImageToStorage(file);  // 이미지 업로드 로직 구현 필요
        member.setProfilePic(imageUrl);
        return memberRepository.save(member);
    }

    private String uploadImageToStorage(MultipartFile file) {
        // 이미지 업로드 로직 구현
        // 예: S3, 로컬 파일 시스템 등에 업로드하고 URL 반환
        return "uploaded_image_url";
    }


}
