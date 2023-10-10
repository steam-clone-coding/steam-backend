package com.clonecoding.steam.dto.order;

import lombok.Data;

public class CartDTO {


    @Data
    public static class Preview{
        private String id;
        private String thumbnailUrl;
        private String name;
        private Integer netPrice;
        private Integer salePrice;
        private Double saleRate;

    }
}
