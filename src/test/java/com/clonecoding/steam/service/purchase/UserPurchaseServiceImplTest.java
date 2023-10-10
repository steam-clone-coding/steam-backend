package com.clonecoding.steam.service.purchase;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.purchase.CartRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

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

    private static User testUser;
    private static Game testGame;


    @BeforeAll
    static void beforeAll() {
        testGame = Game.builder()
                .id(1L)
                .uid("testGame")
                .name("game 1")
                .build();

        testUser = User.builder()
                .id(1L)
                .uid("testUser")
                .build();

    }


    @AfterEach
    void tearDown() {
        cartRepository.deleteAllInBatch();
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
        assertThat(result.getCount()).isEqualTo(1);
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


        @Bean
        public UserPurchaseService userPurchaseService(){
            return new UserPurchaseServiceImpl(cartRepository, userRepository, gameRepository);
        }


    }

}