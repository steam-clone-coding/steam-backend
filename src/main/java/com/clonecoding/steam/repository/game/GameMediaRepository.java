package com.clonecoding.steam.repository.game;

import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.GameMedia;
import com.clonecoding.steam.enums.game.GameMediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameMediaRepository extends JpaRepository<GameMedia, Long> {
    /**
     * 게임 ID를 기반으로 게임 베너 이미지 리스트를 가져오는 메서드
     * @param gameId
     * @param mediaType
     * @return 베너 이미지 리스트
     */ 
    List<GameMedia> findByGameIdAndMediaType(Long gameId, GameMediaType mediaType);
}
