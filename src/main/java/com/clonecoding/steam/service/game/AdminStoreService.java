package com.clonecoding.steam.service.game;

import com.clonecoding.steam.dto.PaginationListDto;
import com.clonecoding.steam.dto.game.DiscountPolicyDTO;
import com.clonecoding.steam.dto.game.GameDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminStoreService {


    /**
     * @author minseok kim
     * @description
     * @param info 할인정책 추가시 필요한 정보
     * @param adminId 할인정책을 추가하려는 어드민의 ID
     * @return
     * @exception
    */
    void addDiscountPolicy(DiscountPolicyDTO.Create info, String adminId);


    // void editDiscountPolicy();



    /**
     * @author minseok kim
     * @description 할인정책 조회
     * @param query 검색어
     * @param page 페이지네이션
     * @return
     * @exception
    */
    PaginationListDto<DiscountPolicyDTO.Preview> getDiscountPolicyList(String query, Pageable page);


/**
 * @author minseok kim
 * @description 게임 추가 요청에 대한 상태 변경
 * @param updateStatus 변경하려는 상태값
 * @return
 * @exception
*/
    void updateGameRequestStatus(GameDTO.CreateRequestStatusUpdate updateStatus);


    /**
     * @author minseok kim
     * @description 게임 추가 요청 목록 전체 조회
     * @param
     * @return
     * @exception
    */
    List<GameDTO.RequestedPreview> getRequestedGameList();

    /**
     * @author minseok kim
     * @description 게임 추가 요청 상세 조회
     * @param gameId 상세 조회하려는 GameID
     * @return
     * @exception
    */
    GameDTO.CreateRequested getRequestedGameDetail(String gameId);

}
