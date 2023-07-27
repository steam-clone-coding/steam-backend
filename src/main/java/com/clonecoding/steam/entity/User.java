package com.clonecoding.steam.entity;


import com.clonecoding.steam.enums.UserAuthority;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    private String username;

    private String password;

    private String uid;

    private UserAuthority userRole;

    private String salt;
}
