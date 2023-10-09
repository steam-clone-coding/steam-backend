package com.clonecoding.steam.entity.game;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "game_medias")
public class GameMedia {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_medias_id_seq")
    @SequenceGenerator(name = "game_medias_id_seq", sequenceName = "game_medias_id_seq", allocationSize = 1)

    @Column(name = "game_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "media_type", nullable = false)
    private String mediaType;
}