package com.clonecoding.steam.dto.game;

public class ReviewSearchConditions {

    public static enum ReviewType{
        ALL, POSITIVE, NEGATIVE
    }
    public static enum PurchaseType{
        ALL, STEAM_PURCHASER, OTHERS
    }

    public static enum Language{
        ALL, YOUR_LANGUAGE
    }
}
