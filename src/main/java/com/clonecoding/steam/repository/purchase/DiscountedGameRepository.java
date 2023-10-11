package com.clonecoding.steam.repository.purchase;

import com.clonecoding.steam.entity.purchase.DiscountedGame;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DiscountedGameRepository extends JpaRepository<DiscountedGame, Long> {

}
