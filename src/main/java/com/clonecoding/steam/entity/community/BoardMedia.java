package com.clonecoding.steam.entity.community;

import com.clonecoding.steam.enums.community.MediaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "board_media_id")
    private Long id;


    private String mediaUrl;
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;
}
