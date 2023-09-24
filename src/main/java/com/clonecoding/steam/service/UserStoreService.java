package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.GameDTO;
import org.springframework.data.domain.Pageable;

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
    List<GameDTO.Preview> search(String query, Pageable page);



}
