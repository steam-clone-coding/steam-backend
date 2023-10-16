package com.clonecoding.steam.dto.order;

import com.clonecoding.steam.entity.game.GameCategory;
import com.clonecoding.steam.entity.purchase.Cart;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CartDTO {


    @Data
    @Builder(access = AccessLevel.PRIVATE)
    public static class Preview{
        private String id;
        private String thumbnailUrl;
        private String name;
        private Integer netPrice;
        private Integer salePrice;
        private Double saleRate;

        public static Preview entityToDto(Cart entity) {
            String thumbnailUrl = null;
            final DiscountedGame activateDiscount = entity.getGame().getActivateDiscount(LocalDateTime.now());

            return Preview.builder()
                    .id(entity.getUid())
                    .thumbnailUrl(thumbnailUrl)
                    .name(entity.getGame().getName())
                    .netPrice(entity.getGame().getPrice())
                    .build();
        }
    }
}
