package com.clonecoding.steam.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.Requirements;
import com.clonecoding.steam.entity.game.Review;
import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameStatus;
import com.clonecoding.steam.enums.purchase.DiscountTypes;
import com.clonecoding.steam.repository.game.GameRepository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private GameRepository gameRepository;

    private User testUser;
    private Requirements testRequirement;

    @BeforeEach
    public void setUp() {
        testRequirement = Requirements.builder().build();
        testUser = User.builder()
                .username("test-user")
                .email("hello@naver.com")
                .uid("testuid")
                .build();
        entityManager.persist(testRequirement);
        entityManager.persist(testUser);
    }

    @Test
    @DisplayName("UID로 게임 찾기")
    public void testFindByUid() {
        // Given: UID로 구별되는 게임 생성 및 저장
        createGame("UniqueGameUid", "Unique Game", 10000, LocalDate.now());

        // When: 해당 ID로 게임 조회
        Optional<Game> foundGame = gameRepository.findByUid("UniqueGameUid");

        // Then: 정확한 게임이 검색됨
        assertTrue(foundGame.isPresent());
        assertEquals("Unique Game", foundGame.get().getName());
    }

    @Test
    @DisplayName("특정 이름을 포함하는 게임 찾기")
    public void testFindByNameContaining() {
        // Given: 특정 이름을 포함하는 여러 게임 생성 및 저장
        createGame("SearchGameUid1", "Search Game 1", 10000, LocalDate.now());
        createGame("SearchGameUid2", "Another Search Game", 15000, LocalDate.now());

        // When: 해당 이름으로 게임 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<Game> games = gameRepository.findByNameContaining("Search", pageable);

        // Then: 일치하는 여러 게임이 검색됨
        assertFalse(games.isEmpty());
        assertTrue(games.getTotalElements() >= 2);
    }

    @Test
    @DisplayName("랜덤으로 관련 게임 찾기")
    public void testFindRelatedGamesRandomly() {
        // Given: 여러 게임 생성 및 저장
        for (int i = 0; i < 10; i++) {
            createGame("RelatedGameUid" + i, "Related Game " + i, 20000, LocalDate.now());
        }

        // When: 랜덤으로 관련 게임 조회
        List<Game> relatedGames = gameRepository.findRelatedGamesRandomly(1L);

        // Then: 지정된 수의 관련 게임이 검색됨
        assertEquals(5, relatedGames.size());
    }

    @Test
    @DisplayName("모든 게임 조회")
    public void testFindAll() {
        // Given: 여러 게임 생성 및 저장
        createGame("Game1Uid", "Game 1", 15000, LocalDate.now());
        createGame("Game2Uid", "Game 2", 20000, LocalDate.now());

        // When: 모든 게임 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<Game> allGames = gameRepository.findAll(pageable);

        // Then: 최소한 2개 이상의 게임이 검색됨
        assertTrue(allGames.getTotalElements() >= 2);
    }

    @ParameterizedTest
    @CsvSource({
            "FreeGameUid, Free Game, 0, 0",  // 오늘 출시된, 가격이 0원인 게임
            "CheapGameUid, Cheap Game, 10000, 0", // 오늘 출시된, 가격이 만원 이하인 게임
            "NewGameUid, New Game, 10000, -5" // 5일 전에 출시된 게임
    })
    @DisplayName("가격과 출시일에 따라 게임 조회")
    public void testFindByPriceAndReleaseDate(String uid, String name, int price, int daysAgo) {
        // Given: 지정된 가격과 출시일을 갖는 게임 생성 및 저장
        LocalDate releaseDate = LocalDate.now().minusDays(daysAgo);
        createGame(uid, name, price, releaseDate);

        // When: 가격과 출시일을 기반으로 게임 조회
        Page<Game> resultGames = fetchGamesBasedOnPriceAndReleaseDate(price, daysAgo);

        // Then: 일치하는 게임이 검색됨
        verifyGames(resultGames, name);
    }

    @ParameterizedTest
    @CsvSource({
            "DiscountGameUid1, Discount Game 1, 20000, FIXED, 10000",  // 50% 할인
            "DiscountGameUid2, Discount Game 2, 20000, PERCENT, 50"    // 50% 할인율
    })
    @DisplayName("특별 할인 적용 게임 조회")
    public void testFindGamesWithSpecialDiscount(String uid, String name, int price, DiscountTypes discountType,
                                                 int discountValue) {
        // Given: 특별 할인이 적용된 게임 생성 및 저장
        Game game = createGame(uid, name, price, LocalDate.now());
        addDiscountToGame(game, discountType, discountValue);

        // When: 특별 할인이 적용된 게임 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<Game> discountedGames = gameRepository.findGamesWithSpecialDiscount(pageable);

        // Then: 할인이 적용된 게임이 검색됨
        assertFalse(discountedGames.isEmpty());
        assertTrue(discountedGames.getContent().stream().anyMatch(g -> g.getName().equals(name)));
    }

    @ParameterizedTest
    @CsvSource({
            "RecommendedGameUid1, Recommended Game 1, 95, 10", // 평점 95, 리뷰 수 10개
            "RecommendedGameUid2, Recommended Game 2, 90, 12", // 평점 90, 리뷰 수 12개
            "RecommendedGameUid3, Recommended Game 3, 85, 15", // 평점 85, 리뷰 수 15개 - 이 경우 추천 게임 목록에 나타나지 않아야 함
            "RecommendedGameUid4, Recommended Game 4, 100, 9"   // 평점 100, 리뷰 수 9개 - 이 경우 추천 게임 목록에 나타나지 않아야 함
    })
    @DisplayName("추천 게임 조회")
    public void testFindRecommendedGames(String uid, String name, int rating, int reviewCount) {
        // Given: 리뷰와 평점을 가진 게임 생성 및 저장
        prepareGameWithReviews(uid, name, rating, reviewCount);

        // When: 추천 게임 조회
        Page<Game> recommendedGames = fetchRecommendedGames();

        // Then: 추천 게임 목록에 적절한 게임이 검색됨
        verifyGameInRecommendedList(recommendedGames, name, rating, reviewCount);
    }

    private Game createGame(String uid, String name, int price, LocalDate releaseDate) {
        Game game = Game.builder()
                .uid(uid)
                .name(name)
                .price(price)
                .releaseDate(releaseDate)
                .developer(testUser)
                .status(GameStatus.COMPLETED)
                .requirements(testRequirement)
                .build();
        return entityManager.persist(game);
    }

    private void addDiscountToGame(Game game, DiscountTypes discountType, int discountValue) {
        DiscountPolicy discountPolicy = DiscountPolicy.builder()
                .discountName("특별할인")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .discountType(discountType)
                .startDate(new Timestamp(System.currentTimeMillis() - 86400000))
                .endDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .build();
        entityManager.persist(discountPolicy);

        DiscountedGame discountedGame = switch (discountType) {
            case PERCENT -> buildDiscountedGame(game, discountPolicy, null, (float) discountValue);
            case FIXED -> buildDiscountedGame(game, discountPolicy, discountValue, null);
        };
        entityManager.persist(discountedGame);
    }

    private DiscountedGame buildDiscountedGame(Game game, DiscountPolicy discountPolicy, Integer fixedDiscount,
                                               Float rateDiscount) {
        return DiscountedGame.builder()
                .game(game)
                .discountPolicy(discountPolicy)
                .fixDiscountPrice(fixedDiscount)
                .rateDiscountRate(rateDiscount)
                .build();
    }

    private void addReviewToGame(Game game, int rating) {
        Review review = Review.builder()
                .game(game)
                .user(testUser)
                .rating(rating)
                .description("Test review")
                .build();
        entityManager.persist(review);
    }

    private Page<Game> fetchGamesBasedOnPriceAndReleaseDate(int price, int daysAgo) {
        Pageable pageable = PageRequest.of(0, 10);
        if (price == 0) {
            return gameRepository.findByPrice(price, pageable);
        }
        if (daysAgo >= -7) {
            return gameRepository.findNewReleasedGames(LocalDate.now().minusDays(7), pageable);
        }
        return gameRepository.findByPriceLessThanEqual(price, pageable);
    }

    private void verifyGames(Page<Game> games, String expectedName) {
        assertFalse(games.isEmpty());
        assertEquals(1, games.getTotalElements());
        assertEquals(expectedName, games.getContent().get(0).getName());
    }

    private void prepareGameWithReviews(String uid, String name, int rating, int reviewCount) {
        Game game = createGame(uid, name, 20000, LocalDate.now());
        for (int i = 0; i < reviewCount; i++) {
            addReviewToGame(game, rating);
        }
    }

    private Page<Game> fetchRecommendedGames() {
        Pageable pageable = PageRequest.of(0, 10);
        return gameRepository.findRecommendedGames(pageable);
    }

    private void verifyGameInRecommendedList(Page<Game> recommendedGames, String name, int rating, int reviewCount) {
        if (isEligibleForRecommendation(rating, reviewCount)) {
            assertFalse(recommendedGames.isEmpty());
            assertTrue(recommendedGames.getContent().stream().anyMatch(g -> g.getName().equals(name)));
        } else {
            assertTrue(recommendedGames.getContent().stream().noneMatch(g -> g.getName().equals(name)));
        }
    }

    private boolean isEligibleForRecommendation(int rating, int reviewCount) {
        return rating >= 90 && reviewCount >= 10;
    }
}
