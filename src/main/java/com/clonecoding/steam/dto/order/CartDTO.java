package com.clonecoding.steam.dto.order;

import com.clonecoding.steam.entity.purchase.Cart;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

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
            return null;
        }
    }
}
