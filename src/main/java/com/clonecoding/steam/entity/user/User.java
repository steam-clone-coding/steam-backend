package com.clonecoding.steam.entity.user;


import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.GameLike;
import com.clonecoding.steam.enums.auth.LoginType;
import com.clonecoding.steam.enums.user.UserAuthority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@Table(name = "users")
public class User {


    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private UserWallet wallet;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    private String name;

    private Integer age;

    private String email;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String profile_image;

    @Column(unique = true)
    private String nickname;

    private String username;

    @Column(length = 3000)
    private String password;

    private String uid;

    @Enumerated(EnumType.STRING)
    private UserAuthority userRole;

    private String salt;

    @Builder.Default
    private LocalDateTime lastLoginTime = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static User createUser(String name, String email, String profileImage, LoginType loginType, String uid){
        return User.builder()
                .name(name)
                .nickname("OAuth2TestUserNickName")
                .age(null)
                .email(email)
                .profile_image(profileImage)
                .loginType(loginType)
                .uid(uid)
                .userRole(UserAuthority.ROLE_USER)
                .build();
    }

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> developedGames;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameLike> likedGames;

    public void likeGame(Game game) {
        if (likedGames == null) {
            likedGames = new ArrayList<>();
        }
        GameLike gameLike = GameLike.builder().user(this).game(game).build();
        likedGames.add(gameLike);
        game.getLikedByUsers().add(gameLike); // Game 엔티티도 업데이트
    }

    public void unlikeGame(Game game) {
        if (likedGames != null) {
            GameLike gameLike = GameLike.builder().user(this).game(game).build();
            likedGames.remove(gameLike);
            game.getLikedByUsers().remove(gameLike); // Game 엔티티도 업데이트
        }
    }

}