package com.clonecoding.steam.entity.game;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "requirements")
public class Requirements {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requirements_id_seq")
    @SequenceGenerator(name = "requirements_id_seq", sequenceName = "requirements_id_seq", allocationSize = 1)
    @Column(name = "requirement_id")
    private Long id;

    @Column(name = "pc_requirements")
    private String pcRequirements;

    @Column(name = "mac_requirements")
    private String macRequirements;

    @Column(name = "linux_requirements")
    private String linuxRequirements;

    @Column(name = "minimum")
    private String minimum;

    @Column(name = "recommended")
    private String recommended;
}
