package com.clonecoding.steam.repository.game;

import com.clonecoding.steam.entity.game.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    Page<Review> findByGameIdAndRating(Long gameId, int rating, Pageable pageable);
}