package com.clonecoding.steam.repository.game;

import com.clonecoding.steam.entity.game.Game;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Optional<Game> findByUid(String uid);


    /**
     * 게임 검색
     *
     * @param q        검색어 쿼리
     * @param pageable 객체
     * @return 페이지 반환
     */
    Page<Game> findByNameContaining(String q, Pageable pageable);


    /**
     * 해당 게임과 관련있는 게임을 5개 찾는 API, 지금은 랜덤 게임임.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM games WHERE game_id != ?1 ORDER BY RANDOM() LIMIT 5")
    List<Game> findRelatedGamesRandomly(Long uid);

    // SPECIAL_DISCOUNT: 50% 이상 할인하는 게임 조회
    @Query("SELECT g FROM Game g JOIN g.discountedGames dg JOIN dg.discountPolicy dp " +
            "WHERE dp.startDate <= CURRENT_TIMESTAMP AND dp.endDate >= CURRENT_TIMESTAMP AND " +
            "((dp.discountType = com.clonecoding.steam.enums.purchase.DiscountTypes.PERCENT AND dg.rateDiscountRate >= 50) OR "
            +
            "(dp.discountType = com.clonecoding.steam.enums.purchase.DiscountTypes.FIXED AND g.price - dg.fixDiscountPrice >= g.price / 2))")
    Page<Game> findGamesWithSpecialDiscount(Pageable pageable);

    // 추천 수가 10개 이상인 게임 필터링
    @Query("SELECT g FROM Game g JOIN g.reviews r " +
            "GROUP BY g.id " +
            "HAVING COUNT(g) >= 10 AND AVG(r.rating) >= 90")
    Page<Game> findRecommendedGames(Pageable pageable);

    // FREE_GAME: 가격이 0원인 게임 조회
    Page<Game> findByPrice(Integer price, Pageable pageable);

    // NEW_RELEASED: 최근 7일 내에 출시된 게임 조회
    @Query("SELECT g FROM Game g WHERE g.releaseDate >= :startDate")
    Page<Game> findNewReleasedGames(LocalDate startDate, Pageable pageable);

    // UNDER_10000WON: 가격이 만원 이하인 게임 조회
    Page<Game> findByPriceLessThanEqual(Integer price, Pageable pageable);

    // ALL: 모든 게임 조회
    Page<Game> findAll(Pageable pageable);
}
