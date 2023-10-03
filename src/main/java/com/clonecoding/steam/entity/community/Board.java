package com.clonecoding.steam.entity.community;


import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.community.BoardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "board_id")
    private Long id;

    private String title;

    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String uid;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;
}
