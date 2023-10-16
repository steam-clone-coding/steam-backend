package com.clonecoding.steam.service.purchase;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.game.GameStatus;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.purchase.CartRepository;
import com.clonecoding.steam.repository.purchase.OrderRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.utils.common.NanoIdProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class UserPurchaseServiceImplTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPurchaseService userPurchaseService;

    private User testUser;
    private Game testGame;

    @BeforeEach
    @Transactional
    void setUp() {
        testUser = User.builder()
                .username("test-user")
                .email("hello@naver.com")
                .uid("testuid")
                .build();


        testGame = Game.builder()
                .uid("testGameuid")
                .name("game 1")
                .price(10000)
                .developer(testUser)
                .recentVersion("0.0.1")
                .status(GameStatus.WAIT)
                .build();



        userRepository.save(testUser);
        gameRepository.save(testGame);
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAllInBatch();
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("User의 Cart 목록에 게임을 추가할 수 있다.")
    public void addCart() throws Exception{
        //given

        //when

        //then
        assertThatCode(()->userPurchaseService.addCart(testUser.getUid(), testGame.getUid()))
                .doesNotThrowAnyException();

        assertThat(cartRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("User의 Cart를 추가하려 했는데 User 조회가 실패한다면 Exception을 throw 한다.")
    public void addCartWithInvalidUser() throws Exception{
        //given

        //when
        assertThatThrownBy(()->userPurchaseService.addCart("noUserUid", testGame.getUid()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessages.USER_NOT_FOUND.getMessage());
        //then

    }

    @Test
    @DisplayName("Cart 추가시 Game 조회가 실패하면 Exception을 Throw 한다.")
    public void addCartWithInvalidGame() throws Exception{
        //given

        //when
        assertThatThrownBy(()->userPurchaseService.addCart(testUser.getUid(), "noGameUid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessages.GAME_NOT_FOUND.getMessage());
        //then

    }


    @Test
    @DisplayName("이미 User가 Cart에 추가된 게임을 다시 Cart에 추가하려고 한다면 Exception을 throw한다.")
    public void testDuplicatedGameInCart() throws Exception{
        //given
        userPurchaseService.addCart(testUser.getUid(), testGame.getUid());
        //when
    
        //then
        assertThatThrownBy(()->userPurchaseService.addCart(testUser.getUid(), testGame.getUid()))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("User의 Cart목록을 조회할 수 있다.")
    public void getCartListByUser() throws Exception{
        //given
        userPurchaseService.addCart(testUser.getUid(), testGame.getUid());
        
        //when
        PaginationListDto<CartDTO.Preview> result =
                userPurchaseService.getCartList(testUser.getUid(), PageRequest.of(0, 10));

        //then
        assertThat(result.getCount()).isEqualTo(1L);
        assertThat(result.getData().size()).isEqualTo(1);
        assertThat(result.getData().get(0).getName()).isEqualTo("game 1");
    }

    @Test
    @DisplayName("User Cart목록 조회시, 해당하는 결과가 없다면 빈 배열을 리턴한다.")
    public void getEmptyCartListWithNoData() throws Exception{
        //given

        //when
        PaginationListDto<CartDTO.Preview> result =
                userPurchaseService.getCartList(testUser.getUid(), PageRequest.of(0, 10));

        //then
        assertThat(result.getCount()).isEqualTo(0);
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).isEmpty();

    }



    @TestConfiguration
    public static class TestConfig{

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private GameRepository gameRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private Environment environment;


        @Bean
        public UserPurchaseService userPurchaseService(){
            return new UserPurchaseServiceImpl(
                    cartRepository,
                    userRepository,
                    gameRepository,
                    nanoIdProvider(),
                    orderRepository);
        }

        @Bean
        public NanoIdProvider nanoIdProvider(){
            return new NanoIdProvider(environment);
        }


    }

}