package com.clonecoding.steam.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "friends")
public class Friend {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_id_seq")
    @SequenceGenerator(name = "friend_id_seq", sequenceName = "friend_id_seq", allocationSize = 1)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id2", nullable = false)
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user1;
}
