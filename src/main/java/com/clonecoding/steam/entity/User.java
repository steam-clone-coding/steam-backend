package com.clonecoding.steam.entity;


import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.enums.UserAuthority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {


    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private UserWallet wallet;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    private String name;

    private Integer age;

    private String email;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String profile_image;

    private String nickname;


    private String username;

    private String password;

    private String uid;

    @Enumerated(EnumType.STRING)
    private UserAuthority userRole;

    private String salt;

    @Builder.Default
    private LocalDateTime lastLoginTime = LocalDateTime.now();


    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();


}
