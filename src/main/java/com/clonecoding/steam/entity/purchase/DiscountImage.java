package com.clonecoding.steam.entity.purchase;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "discount_images")
public class DiscountImage {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discount_images_id_seq")
    @SequenceGenerator(name = "discount_images_id_seq", sequenceName = "discount_images_id_seq", allocationSize = 1)
    @Column(name = "discount_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_policy_id", nullable = false)
    private DiscountPolicy discountPolicy;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    // Getter, Setter 및 기타 메서드
}