package com.clonecoding.steam.entity.game;

import com.clonecoding.steam.entity.purchase.DiscountPolicy;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import com.clonecoding.steam.enums.purchase.DiscountTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class GameTest {

    @Test
    @DisplayName("현재 활성화된 할인 정책이 있다면 그것을 조회할 수 있다.")
    void testGetActivateDiscountGame() throws Exception {
        //given
        final Game game = new Game();
        final DiscountPolicy discountPolicy = DiscountPolicy.builder()
                .startDate(Timestamp.valueOf(LocalDateTime.of(2023,10,13,0,0)))
                .endDate(Timestamp.valueOf(LocalDateTime.of(2023,10,14,0,0)))
                .build();
        final DiscountedGame discountedGame = DiscountedGame.builder()
                .discountPolicy(discountPolicy)
                .game(game)
                .build();

        game.addDiscountedGames(discountedGame);

        //when
        final DiscountedGame actual = game.getActivateDiscount(LocalDateTime.of(2023,10,13,12,0));
        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(discountedGame);
    }

    @Test
    @DisplayName("활성화된 할인 정책이 없다면 할인 금액은 0이다.")
    void testNoActiveDiscountPolicy(){
        // given
        final Game game = new Game();
        // when
        int salePrice = game.getSalePrice(LocalDateTime.of(2023, 10, 13, 12,0));
        // then
        assertThat(salePrice).isZero();
    }


    @Test
    @DisplayName("현재 활성화된 고정 금액 할인 정책이 있다면 할인 금액을 알 수 있다.")
    void testGetSalePrice() throws Exception {
        //given
        final Game game = new Game();
        final DiscountPolicy discountPolicy = DiscountPolicy.builder()
                .startDate(Timestamp.valueOf(LocalDateTime.of(2023,10,13,0,0)))
                .endDate(Timestamp.valueOf(LocalDateTime.of(2023,10,14,0,0)))
                .discountType(DiscountTypes.FIXED)
                .build();

        final DiscountedGame discountedGame = DiscountedGame.builder()
                .discountPolicy(discountPolicy)
                .fixDiscountPrice(5000)
                .game(game)
                .build();

        game.addDiscountedGames(discountedGame);

        //when
        int salePrice = game.getSalePrice(LocalDateTime.of(2023, 10, 13, 12,0));

        //then
        assertThat(salePrice).isEqualTo(5000);
    }

    @Test
    @DisplayName("현재 활성화된 % 금액 할인 정책이 있다면 할인 금액을 알 수 있다.")
    void testGetRateSalePrice(){
        // given
        final Game game = Game.builder()
                .price(10000)
                .build();

        final DiscountPolicy discountPolicy = DiscountPolicy.builder()
                .startDate(Timestamp.valueOf(LocalDateTime.of(2023,10,13,0,0)))
                .endDate(Timestamp.valueOf(LocalDateTime.of(2023,10,14,0,0)))
                .discountType(DiscountTypes.PERCENT)
                .build();

        final DiscountedGame discountedGame = DiscountedGame.builder()
                .discountPolicy(discountPolicy)
                .rateDiscountRate(0.5f)
                .game(game)
                .build();

        game.addDiscountedGames(discountedGame);
        //when
        int salePrice = game.getSalePrice(LocalDateTime.of(2023, 10, 13, 12,0));

        //then
        assertThat(salePrice).isEqualTo(5000);
    }


}