package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> likes;

    public void addLike(ReviewLike like) {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        likes.add(like);
        like.setReview(this); // 양방향 연관관계 설정
    }

    public void removeLike(ReviewLike like) {
        if (likes != null) {
            likes.remove(like);
            like.setReview(null); // 양방향 연관관계 해제
        }
    }
}
