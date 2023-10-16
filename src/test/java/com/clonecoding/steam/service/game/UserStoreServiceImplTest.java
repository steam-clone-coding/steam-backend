package com.clonecoding.steam.service.game;

import com.clonecoding.steam.IntegrationTestSupport;
import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.game.GameDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.GameMedia;
import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameMediaType;
import com.clonecoding.steam.enums.game.GameStatus;
import com.clonecoding.steam.enums.purchase.DiscountTypes;
import com.clonecoding.steam.repository.game.GameMediaRepository;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.purchase.DiscountPolicyRepository;
import com.clonecoding.steam.repository.purchase.DiscountedGameRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserStoreServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private UserStoreServiceImpl userStoreService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMediaRepository gameMediaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountPolicyRepository discountPolicyRepository;

    @Autowired
    private DiscountedGameRepository discountedGameRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .username("test-user")
                .email("hello@naver.com")
                .uid("testuid")
                .build();

        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        discountedGameRepository.deleteAll();
        discountPolicyRepository.deleteAll();
    }

    @Test
    @DisplayName("query로 받은 검색어로 게임 리스트를 반환할 수 있다.")
    void searchTest() {

        //given
        List<Game> gameList = List.of(
                createGame("aaaaa11111", "test1", 1000),
                createGame("bbbbb22222", "test2", 2000),
                createGame("ccccc33333","test3",  3000),
                createGame("ddddd44444", "NoSearchCase", 4000)
        );

        List<GameMedia> gameMediaList = List.of(
                createGameMedia(gameList.get(0), "t1ThumbnailImageUrl", GameMediaType.HEADER_IMAGE),
                createGameMedia(gameList.get(0), "t1MediaUrl", GameMediaType.SCREEN_SHOT),
                createGameMedia(gameList.get(1), "t2MediaUrl", GameMediaType.SCREEN_SHOT),
                createGameMedia(gameList.get(2), "t3ThumbnailImageUrl", GameMediaType.HEADER_IMAGE),
                createGameMedia(gameList.get(3), "t4ThumbnailImageUrl", GameMediaType.HEADER_IMAGE)
        );

        gameRepository.saveAll(gameList);
        gameMediaRepository.saveAll(gameMediaList);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        PaginationListDto<GameDTO.Search> response = userStoreService.search("test", pageable);

        //then
        assertThat(response.getCount()).isEqualTo(3L);
        assertThat(response.getData().size()).isEqualTo(3);


        List<String> gameNames = response.getData().stream()
                .map(GameDTO.Search::getName)
                .toList();

        assertThat(gameNames)
                .allMatch(name -> name.contains("test"));
    }

    @Test
    @DisplayName("game uid를 이용해 게임 상세 조회를 할 수 있다.")
    void getGameDetail(){
        //given
        Game game = createGame("aaaaa11111", "test1", 1000);

        gameRepository.save(game);

        List<DiscountPolicy> discountPolicyList = List.of(
                createDiscount("testDiscount1", DiscountTypes.PERCENT, LocalDateTime.of(2023, 10,17, 10,10)),
                createDiscount("testDiscount2", DiscountTypes.PERCENT, LocalDateTime.of(2023, 10,16, 10,10)),
                createDiscount("testDiscount3", DiscountTypes.PERCENT, LocalDateTime.of(2023, 10,18, 10,10)),
                createDiscount("testDiscount4", DiscountTypes.FIXED, LocalDateTime.of(2023, 10,17, 10,10)),
                createDiscount("testDiscount5", DiscountTypes.FIXED, LocalDateTime.of(2023, 10,16, 10,10))
        );

        List<DiscountedGame> discountedGameList = List.of(
                createDiscountedGame(game, discountPolicyList.get(0), 0.6F),
                createDiscountedGame(game, discountPolicyList.get(1), 0.2F),
                createDiscountedGame(game, discountPolicyList.get(2), 0.3F), // -> testDiscount3가 선택되야함
                createDiscountedGame(game, discountPolicyList.get(3), 0.4F),
                createDiscountedGame(game, discountPolicyList.get(4), 0.5F)
        );

        discountPolicyRepository.saveAll(discountPolicyList);
        discountedGameRepository.saveAll(discountedGameList);

        game.setDiscountedGames(discountedGameList);

        //when
        GameDTO.Detail response = userStoreService.getGameDetail(game.getUid(), LocalDateTime.of(2023, 10, 17,10,11));


        //then
        assertThat(response.getSaleRate()).isEqualTo(0.3F);
        assertThat(response.getSalePrice()).isEqualTo(700);


    }

    private static DiscountedGame createDiscountedGame(Game game, DiscountPolicy discountPolicy, Float discountRate) {
        return DiscountedGame.builder()
                .game(game)
                .discountPolicy(discountPolicy)
                .rateDiscountRate(discountRate)
                .build();
    }

    private static DiscountPolicy createDiscount(String name, DiscountTypes type, LocalDateTime endDate) {
        return DiscountPolicy.builder()
                .discountName(name)
                .discountType(type)
                .startDate(Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1, 1)))
                .endDate(Timestamp.valueOf(endDate))
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }


    public Game createGame(String uid, String name, Integer price){
        return Game.builder()
                .uid(uid)
                .name(name)
                .developer(testUser)
                .price(price)
                .status(GameStatus.COMPLETED)
                .build();
    }

    public GameMedia createGameMedia(Game game, String mediaUrl, GameMediaType gameMediaType){
        return GameMedia.builder()
                .game(game)
                .mediaUrl(mediaUrl)
                .mediaType(gameMediaType)
                .build();
    }

}