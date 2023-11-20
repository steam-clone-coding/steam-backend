package com.clonecoding.steam.entity.purchase;

import com.clonecoding.steam.entity.user.Country;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.purchase.PayMethod;
import com.clonecoding.steam.enums.purchase.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_id_seq")
    @SequenceGenerator(name = "orders_id_seq", sequenceName = "orders_id_seq", allocationSize = 1)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "purchase_status", nullable = false, columnDefinition = "varchar(255) CHECK (purchase_status IN ('PURCHASE_COMPLETE', 'REFUND'))")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}