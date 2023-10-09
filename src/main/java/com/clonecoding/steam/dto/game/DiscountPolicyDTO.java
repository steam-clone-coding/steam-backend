package com.clonecoding.steam.dto.game;

import java.time.LocalDate;

public class DiscountPolicyDTO {

    public static class Create {
        private String name;
        private DiscountType discountType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imageUrl;
    }


    public static class Preview{
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imageUrl;
    }

    public static enum DiscountType {
        FIX, RATE
    }
}
