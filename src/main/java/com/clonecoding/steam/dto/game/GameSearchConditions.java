package com.clonecoding.steam.dto.game;

public class GameSearchConditions {

    public static enum SortBy{
        RELEVANCE, RELEASE_DATE, NAME,
        LOWEST_PRICE, HIGHEST_PRICE, USER_REIVEWS
    }

    public static enum ListType{
        SPECIAL_DISCOUNT, RECOMMEND, FREE_GAME,
        NEW_RELEASED, COMING_SOON, UNDER_10000WON, ALL
    }

    public static enum CategoryType{
        SCIENCE_FICTION, PUZZLE, ADVENTURE,
        STRATEGY, HORROR,  ROUGE_LIKE_ROUGE_LITE,
        GREAT_ON_DECK, OPEN_WORLD, CITY_SETTLEMENT,
        VISUAL_NOVEL, VR, SPORTS,
        RACING, ROLE_PLAYING, CASUAL,
        FIGHTING, SURVIVAL,  ANIMATION, SIMULATION,
        ALL
    }

}
