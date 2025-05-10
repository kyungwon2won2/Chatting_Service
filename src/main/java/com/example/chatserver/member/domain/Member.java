package com.example.chatserver.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    //관리자는 현재 사용안하기 때문에 일단은 user로 default 설정
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
}
