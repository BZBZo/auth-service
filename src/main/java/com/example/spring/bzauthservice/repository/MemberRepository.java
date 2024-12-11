package com.example.spring.bzauthservice.repository;

import com.example.spring.bzauthservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndProvider(String email, String provider);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByBusinessNumber(String businessNumber);
    Optional<Member> findByPhoneAndUserRole(String phone, String role);
}
