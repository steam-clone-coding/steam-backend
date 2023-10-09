package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "games")
public class Game {


    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "games_id_seq")
    @SequenceGenerator(name = "games_id_seq", sequenceName = "games_id_seq", allocationSize = 1)
    @Column(name = "game_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "description")
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = false)
    private User developer;

    @Column(name = "release_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate releaseDate;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "recent_version", nullable = false)
    private String recentVersion;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column(name = "required_age", nullable = false)
    private int requiredAge = 0;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private Requirements requirements;



    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameMedia> gameMedias;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameCategory> gameCategories;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameLike> likedByUsers;


    public void setDeveloper(User developer) {
        this.developer = developer;
        developer.getDevelopedGames().add(this);
    }

    public void addGameMedia(GameMedia gameMedia) {
        if (this.gameMedias == null) {
            this.gameMedias = new ArrayList<>();
        }
        this.gameMedias.add(gameMedia);
        gameMedia.setGame(this);
    }
}