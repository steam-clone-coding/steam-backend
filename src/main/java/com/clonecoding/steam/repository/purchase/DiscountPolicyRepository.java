package com.clonecoding.steam.repository.purchase;

import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {

}
