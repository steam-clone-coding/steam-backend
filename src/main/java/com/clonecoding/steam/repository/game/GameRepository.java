package com.clonecoding.steam.repository.game;

import com.clonecoding.steam.entity.game.Game;
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
     * @param q 검색어 쿼리
     * @param pageable 객체
     * @return 페이지 반환
     */
    Page<Game> findByNameContaining(String q, Pageable pageable);


    /**
     * 해당 게임과 관련있는 게임을 5개 찾는 API, 지금은 랜덤 게임임.
     * @param gameId
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM games WHERE game_id != ?1 ORDER BY RANDOM() LIMIT 5")
    List<Game> findRelatedGamesRandomly(Long uid);
}
