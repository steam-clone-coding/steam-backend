package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameMediaType;
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
@Builder
@NoArgsConstructor
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

    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    @Builder.Default
    private LocalDate releaseDate = LocalDate.now();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "uid", nullable = false)
    private String uid;

    @Column(name = "recent_version")
    private String recentVersion;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column(name = "required_age")
    @Builder.Default
    private int requiredAge = 0;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private Requirements requirements;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameMedia> gameMedias = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameCategory> gameCategories = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameLike> likedByUsers = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiscountedGame> discountedGames = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

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


    /*
     * @description 게임의 유효한 할인 정책이 존재하는 경우, 할인 금액을 계산하는 메서드
     * @author minseok kim
     * @param now 메서드를 호출한 시간
     * @throws
    */
    public int getSalePrice(LocalDateTime now) {
        final DiscountedGame discountedGame = getActivateDiscount(now);
        if(discountedGame == null){
            return 0;
        }

        if(discountedGame.getDiscountPolicy().getDiscountType() == DiscountTypes.FIXED){
            return discountedGame.getFixDiscountPrice();
        }
        else if(discountedGame.getDiscountPolicy().getDiscountType() == DiscountTypes.PERCENT){
            return (int) (discountedGame.getRateDiscountRate() * price);
        }

        return 0;
    }


    /*
     * @description 게임의 유효한 할인 정책이 존재하는 경우, 할인률을 계산하는 메서드
     * @author minseok kim
     * @param now 메서드를 호출한 시간
     * @throws
     */
    public double getDiscountRate(LocalDateTime now){
        DiscountedGame activateDiscount = getActivateDiscount(now);

        if(activateDiscount == null){
            return 0.0;
        }

        if(activateDiscount.getDiscountPolicy().getDiscountType() == DiscountTypes.FIXED){
            return (double)  activateDiscount.getFixDiscountPrice() / price;
        }
        else if(activateDiscount.getDiscountPolicy().getDiscountType() == DiscountTypes.PERCENT){
            return activateDiscount.getRateDiscountRate();
        }

        return 0.0;
    }



    /*
     * @description 게임의 썸네일 이미지를 조회하는 메서드
     * @author minseok kim
     */
    public String getThumbnail() {
        for (GameMedia gameMedia: gameMedias) {
            if(gameMedia.getMediaType() == GameMediaType.HEADER_IMAGE){
                return gameMedia.getMediaUrl();
            }
        }

        return null;
    }

    /**
     * @methodName calculateAverageRating
     * @description 게임의 평균 평점을 계산하는 메서드
     * @return double 평균 평점
     */
    public double calculateAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = reviews.stream()
                .mapToDouble(Review::getRating) // Review 엔티티에 getRating 메서드가 정의되어 있다고 가정
                .sum();

        return totalRating / reviews.size();
    }
}
