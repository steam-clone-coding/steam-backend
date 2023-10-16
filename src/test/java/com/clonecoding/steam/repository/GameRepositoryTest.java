package com.clonecoding.steam.repository;

import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.Requirements;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameStatus;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    private Requirements testRequirement;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testRequirement = Requirements.builder()
                .build();

        testUser = User.builder()
                .username("test-user")
                .email("hello@naver.com")
                .uid("testuid")
                .build();

        entityManager.persist(testRequirement);
        entityManager.persist(testUser);
    }

    private Game createGame(String uid, String name) {
        return Game.builder()
                .uid(uid)
                .name(name)
                .price(1000)
                .developer(testUser)
                .status(GameStatus.COMPLETED)
                .requirements(testRequirement)
                .build();
    }

    @Test
    public void testFindByUid() {
        Game game1 = createGame("testUid", "Gametest");
        entityManager.persist(game1);

        Optional<Game> foundGame = gameRepository.findByUid("testUid");

        // 검증
        assertTrue(foundGame.isPresent());
        assertEquals("testUid", foundGame.get().getUid());
    }

    @Test
    public void testFindByNameContaining() {
        Game game1 = createGame("Game1Uid", "Game1");
        Game game2 = createGame("Game2Uid", "Game2");
        entityManager.persist(game1);
        entityManager.persist(game2);

        // 테스트 실행
        Pageable pageable = PageRequest.of(0, 10);
        Page<Game> games = gameRepository.findByNameContaining("Game", pageable);

        // 검증
        assertEquals(2, games.getTotalElements());
    }

    @Test
    public void testFindRelatedGamesRandomly() {
        // 테스트 데이터 삽입
        for (int i = 0; i < 10; i++) {
            Game game = createGame("GameUid" + i, "Game" + i);
            entityManager.persist(game);
        }

        // 테스트 실행
        List<Game> relatedGames = gameRepository.findRelatedGamesRandomly(1L);

        // 검증
        assertEquals(5, relatedGames.size());
    }
}
