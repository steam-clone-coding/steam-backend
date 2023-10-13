package com.clonecoding.steam.repository;

import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.Requirements;
import com.clonecoding.steam.entity.game.Review;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameStatus;
import com.clonecoding.steam.repository.game.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Game testGame;
    private User testUser;
    private Requirements testRequirement;

    @BeforeEach
    public void setUp() {
        testRequirement = Requirements.builder()
                .build();

        testUser = User.builder()
                .username("test-user")
                .email("hello@naver.com")
                .uid("testuid")
                .build();

        entityManager.persist(testUser);
        entityManager.persist(testRequirement);

        testGame = Game.builder()
                .uid("testGameUid")
                .name("Test Game")
                .price(1000)
                .developer(testUser)
                .status(GameStatus.COMPLETED)
                .requirements(testRequirement)
                .build();

        entityManager.persist(testGame);
    }

    @Test
    public void testSaveReview() {
        Review review = Review.builder()
                .game(testGame)
                .user(testUser)
                .rating(5)
                .description("Great game!")
                .build();

        reviewRepository.save(review);

        Optional<Review> foundReview = reviewRepository.findById(review.getId());

        // 검증
        assertTrue(foundReview.isPresent());
        assertEquals(5, foundReview.get().getRating());
    }

    @Test
    public void testFindByGameIdAndRating() {
        Review review1 = Review.builder()
                .game(testGame)
                .user(testUser)
                .rating(5)
                .description("Great game!")
                .build();

        Review review2 = Review.builder()
                .game(testGame)
                .user(testUser)
                .rating(4)
                .description("Good game!")
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);

        // 테스트 실행
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviews = reviewRepository.findByGameIdAndRating(testGame.getId(), 5, pageable);

        // 검증
        assertEquals(1, reviews.getTotalElements());
    }
}

