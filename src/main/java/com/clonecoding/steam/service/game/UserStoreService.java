package com.clonecoding.steam.service.game;

import com.clonecoding.steam.dto.game.GameDTO;
import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.game.GameSearchConditions;
import com.clonecoding.steam.dto.game.ReviewDTO;
import com.clonecoding.steam.dto.game.ReviewSearchConditions;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserStoreService {


    /**
     * @author minseok kim
     * @description 메인페이지 배너 조회
     * @return 배너 이미지 URL
    */
    List<String> getBannerImages();


    /**
     * @author minseok kim
     * @description 검색 키워드 기반 게임 검색
     * @param query 검색 키워드
     * @param page 페이지네이션
     * @return
     * @exception
    */
    PaginationListDto<GameDTO.Search> search(String query, Pageable page);




    /**
     * @author minseok kim
     * @description GAME 리스트 조회
     * @param query 검색어
     * @param page 페이지네이션
     * @param sortBy 정렬기준
     * @param listType 리스트 타입 필터링 기준
     * @param categoryType 게임 카테고리 필터링 기준
     * @return
     * @exception
    */
    PaginationListDto<GameDTO.Preview> getGameList(
            String query,
            Pageable page,
            GameSearchConditions.SortBy sortBy,
            GameSearchConditions.ListType listType,
            GameSearchConditions.CategoryType categoryType
    );


    /**
     * @author minseok kim
     * @description
     * @param gameId 관련 게임을 조회하려는 GAME의 ID
     * @param page 페이지 네이션
     * @return
     * @exception
    */
    PaginationListDto<GameDTO.Preview> getRelateGameList(String gameId, Pageable page);

    /**
     * @author minseok kim
     * @description Game Detail 조회
     * @param id 조회하려는 Game의 ID
     * @return
     * @exception
    */
    GameDTO.Detail getGameDetail(String id, LocalDateTime now);

    /**
     * @author minseok kim
     * @description Game에 대한 REVIEW 조회
     * @param gameId 리뷰를 조회하려는 Game의 ID
     * @param reviewType 검색 상세조건(긍정 리뷰, 부정 리뷰)
     * @param purchaseType 검색 상세 조건(구매 방식 구분)
     * @param language 검색 상세 조건(언어 구분)
     * @param minimumPlayTime 검색상세조건(최소 플레이타임)\
     * @param page 페이지 네이션
     * @return
     * @exception
    */
    ReviewDTO.PaginationList getReviewList(
            String gameId,
            ReviewSearchConditions.ReviewType reviewType,
            ReviewSearchConditions.PurchaseType purchaseType,
            ReviewSearchConditions.Language language,
            Integer minimumPlayTime,
            Pageable page
    );
}
