package com.clonecoding.steam.entity.purchase;

import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "carts")
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_id_seq")
    @SequenceGenerator(name = "carts_id_seq", sequenceName = "carts_id_seq", allocationSize = 1)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
