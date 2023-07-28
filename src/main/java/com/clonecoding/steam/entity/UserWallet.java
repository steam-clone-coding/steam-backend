package com.clonecoding.steam.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserWallet {


    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_seq")
    @SequenceGenerator(name = "wallet_id_seq", sequenceName = "wallet_id_seq", allocationSize = 1)
    @Column(name = "wallet_id")
    private Long id;

    @Builder.Default
    private Integer balance = 0;
}
