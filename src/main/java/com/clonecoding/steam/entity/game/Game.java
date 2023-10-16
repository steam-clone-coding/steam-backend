package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameStatus;
import com.clonecoding.steam.enums.purchase.DiscountTypes;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
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
    @Builder.Default
    private LocalDate releaseDate = LocalDate.now();

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "recent_version", nullable = false)
    private String recentVersion;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column(name = "required_age", nullable = false)
    @Builder.Default
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

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountedGame> discountedGames = new ArrayList<>();

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

    public void addDiscountedGames(DiscountedGame discountedGame){
        discountedGames.add(discountedGame);
    }

    /**
     * @methodName getActivateDiscount
     * @author Minseok kim
     * @description 현재 게임에 대해 활성화된 할인 정책을 가져오는 메서드
     */
    public DiscountedGame getActivateDiscount(LocalDateTime now){
        final Timestamp nowTimeStamp = Timestamp.valueOf(now);

        for(DiscountedGame discountedGame : discountedGames){
            DiscountPolicy discountPolicy = discountedGame.getDiscountPolicy();

            if(discountPolicy.getStartDate().after(nowTimeStamp) || discountPolicy.getEndDate().before(nowTimeStamp)){
                continue;
            }
            return discountedGame;
        }
        return null;

    }


    public int getSalePrice() {
        final DiscountedGame discountedGame = getActivateDiscount(LocalDateTime.now());
        if(discountedGame == null){
            return 0;
        }

        if(discountedGame.getDiscountPolicy().getDiscountType() == DiscountTypes.FIXED){
            return discountedGame.getFixDiscountPrice();
        }

        return 0;
    }
}