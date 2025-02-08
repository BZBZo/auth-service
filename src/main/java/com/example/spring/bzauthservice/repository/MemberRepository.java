package com.example.spring.bzauthservice.repository;

import com.example.spring.bzauthservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndProvider(String email, String provider);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByBusinessNumber(String businessNumber);
    Optional<Member> findByPhoneAndUserRole(String phone, String role);

    // memberNos에 포함된 모든 회원을 한 번에 조회
    // JpaRepository에서 자동으로 IN 절을 생성해서 쿼리를 실행해준다.
    List<Member> findByMemberNoIn(Set<Long> memberNos);

}
