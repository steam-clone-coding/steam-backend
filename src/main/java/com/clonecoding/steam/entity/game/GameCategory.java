package com.clonecoding.steam.entity.game;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "game_categories")
public class GameCategory {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_categories_id_seq")
    @SequenceGenerator(name = "game_categories_id_seq", sequenceName = "game_categories_id_seq", allocationSize = 1)
    @Column(name = "game_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
