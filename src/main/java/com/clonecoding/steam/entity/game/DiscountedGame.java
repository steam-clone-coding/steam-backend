package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "discounted_games")
public class DiscountedGame {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discounted_games_id_seq")
    @SequenceGenerator(name = "discounted_games_id_seq", sequenceName = "discounted_games_id_seq", allocationSize = 1)
    @Column(name = "discounted_game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_policy_id", nullable = false)
    private DiscountPolicy discountPolicy;

    @Column(name = "fix_discount_price")
    private Integer fixDiscountPrice;

    @Column(name = "rate_discount_rate")
    private Float rateDiscountRate;
}