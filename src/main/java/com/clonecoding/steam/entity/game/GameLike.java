package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games_like")
public class GameLike {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_likes_id_seq")
    @SequenceGenerator(name = "game_likes_id_seq", sequenceName = "game_likes_id_seq", allocationSize = 1)
    @Column(name = "game_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
}