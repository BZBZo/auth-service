package com.example.spring.bzauthservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @Column(nullable = false)
    private String email;

    private String nickname;

    private String phone;

    @Column(nullable = false)
    private String userRole; // 이 필드는 일반적으로 'USER', 'ADMIN', 'SELLER' 같은 값을 저장

    private String provider;

}
