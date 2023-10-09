package com.clonecoding.steam.entity.game;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "game_download_file")
public class GameDownloadFile {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_download_file_id_seq")
    @SequenceGenerator(name = "game_download_file_id_seq", sequenceName = "game_download_file_id_seq", allocationSize = 1)
    @Column(name = "game_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "version", nullable = false)
    private String version;
}
