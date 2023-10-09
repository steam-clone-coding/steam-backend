package com.clonecoding.steam.entity.purchase;

import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.user.Country;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "country_restrictions")
public class CountryRestriction {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restriction_id_seq")
    @SequenceGenerator(name = "restriction_id_seq", sequenceName = "restriction_id_seq", allocationSize = 1)
    @Column(name = "restriction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

}
