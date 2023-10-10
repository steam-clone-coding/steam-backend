package com.clonecoding.steam.entity.purchase;

import com.clonecoding.steam.enums.purchase.DiscountTypes;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "discount_policies")
public class DiscountPolicy {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discount_policies_id_seq")
    @SequenceGenerator(name = "discount_policies_id_seq", sequenceName = "discount_policies_id_seq", allocationSize = 1)
    @Column(name = "discount_policy_id")
    private Long id;

    @Column(nullable = false)
    private String discountName;

    @Column(nullable = false, columnDefinition = "varchar(255) CHECK (discount_type IN ('FIXED', 'PERCENT'))")
    private DiscountTypes discountType;

    @Column(nullable = false)
    private Timestamp startDate;

    @Column(nullable = false)
    private Timestamp endDate;

    @Column(nullable = false)
    private Timestamp createdAt;
}
