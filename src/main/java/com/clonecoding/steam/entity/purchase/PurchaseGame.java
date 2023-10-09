package com.clonecoding.steam.entity.purchase;

import com.clonecoding.steam.entity.game.Game;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "purchase_games")
public class PurchaseGame {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_id_seq")
    @SequenceGenerator(name = "purchase_id_seq", sequenceName = "purchase_id_seq", allocationSize = 1)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discounted_price", nullable = false)
    private Integer discountedPrice;

    @Column(name = "purchase_price", nullable = false)
    private Integer purchasePrice;

    @Column(name = "purchase_status", nullable = false, columnDefinition = "varchar(255) CHECK (purchase_status IN ('PURCHASE_COMPLETE', 'REFUND'))")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;

    // Getter, Setter 및 기타 메서드
}
