package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "review_likes")
public class ReviewLike {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_like_id_seq")
    @SequenceGenerator(name = "review_like_id_seq", sequenceName = "review_like_id_seq", allocationSize = 1)
    @Column(name = "review_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}
