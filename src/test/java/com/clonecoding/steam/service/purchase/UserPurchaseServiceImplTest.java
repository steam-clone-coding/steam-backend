package com.clonecoding.steam.service.purchase;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.purchase.CartRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.utils.common.NanoIdProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
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
                .build();

        testUser = User.builder()
                .id(1L)
                .uid("testUser")
                .build();

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
    @DisplayName("User의 Cart목록을 조회할 수 있다.")
    public void getCartListByUser() throws Exception{
        //given

        //when
        PaginationListDto<CartDTO.Preview> result =
                userPurchaseService.getCartList(testUser.getUid(), PageRequest.of(0, 10));

        //then
        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getData().size()).isEqualTo(1);

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
            return new UserPurchaseServiceImpl(cartRepository, userRepository);
        }


    }

}