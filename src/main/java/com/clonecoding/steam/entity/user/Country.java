package com.clonecoding.steam.entity.user;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Country {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countries_id_seq")
    @SequenceGenerator(name = "countries_id_seq", sequenceName = "countries_id_seq", allocationSize = 1)
    @Column(name = "country_id")
    private Long id;

    @Column(nullable = false)
    private String name;
}
