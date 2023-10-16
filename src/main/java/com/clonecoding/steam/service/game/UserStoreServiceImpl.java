package com.clonecoding.steam.service.game;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.game.GameDTO;
import com.clonecoding.steam.dto.game.GameSearchConditions;
import com.clonecoding.steam.dto.game.ReviewDTO;
import com.clonecoding.steam.dto.game.ReviewSearchConditions;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.GameMedia;
import com.clonecoding.steam.entity.game.Review;
import com.clonecoding.steam.enums.game.GameMediaType;
import com.clonecoding.steam.repository.game.GameMediaRepository;
import com.clonecoding.steam.repository.game.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoreServiceImpl implements UserStoreService{
    private final GameRepository gameRepository;
    private final GameMediaRepository gameMediaRepository;

    @Override
    public List<String> getBannerImages() {
        // TODO : Banner 테이블이 없음
        return null;
    }

    @Override
    public PaginationListDto<GameDTO.Search> search(String query, Pageable page) {
        Page<Game> findGames = gameRepository.findByNameContaining(query, page);

        List<GameDTO.Search> gameDtoList = findGames.stream()
                .map(game -> GameDTO.Search.entityToDto(game, game.getThumbnail()))
                .collect(Collectors.toList());

        return PaginationListDto.<GameDTO.Search>builder()
                .count(findGames.getTotalElements())
                .data(gameDtoList)
                .build();
    }


    @Override
    public PaginationListDto<GameDTO.Preview> getGameList(String query, Pageable page, GameSearchConditions.SortBy sortBy, GameSearchConditions.ListType listType, GameSearchConditions.CategoryType categoryType) {
        // TODO : Discount Category 테이블이 없음
        return null;
    }

    @Override
    public PaginationListDto<GameDTO.Preview> getRelateGameList(String gameId, Pageable page) {
        // TODO : 뭘 추천할지 안정함
        return null;
    }

    @Override
    public GameDTO.Detail getGameDetail(String gameId, LocalDateTime now) {
        Game findGame = gameRepository.findByUid(gameId).get();
        return GameDTO.Detail.entityToDto(findGame, now);
    }

    @Override
    public ReviewDTO.PaginationList getReviewList(String gameId, ReviewSearchConditions.ReviewType reviewType, ReviewSearchConditions.PurchaseType purchaseType, ReviewSearchConditions.Language language, Integer minimumPlayTime, Pageable page) {
        Game findGame = gameRepository.findByUid(gameId).get();
        Page<Review> reviewPage = listToPage(findGame.getReviews(), page);

        // TODO : 리뷰 unlike 없음, 구매타입 없음, 플레이 시간 없음, helpful없음

        return null;
    }


    private <T> Page<T> listToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<T> sublist = list.subList(start, end);

        return new PageImpl<>(sublist, pageable, list.size());
    }
}


