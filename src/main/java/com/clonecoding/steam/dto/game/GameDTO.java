package com.clonecoding.steam.dto.game;

import com.clonecoding.steam.dto.user.DeveloperDTO;

import java.time.LocalDate;
import java.util.List;

public class GameDTO {

    public static class Search {
        private String id;
        private String thumbnailUrl;
        private String name;
        private String price;

    }

    public static class Preview{
        private String id;
        private String thumbnailUrl;
        private String name;
        private Integer netPrice;
        private Integer salePrice;
        private Double saleRate;

    }

    public static class Detail{
        private String id;
        private String thumbnailUrl;

        private List<String> imageUrls;

        private String name;

        private String description;

        private String genre;

        private List<DeveloperDTO.Preview> developers;

        private LocalDate releaseDate;

        private Integer netPrice;

        private Integer salePrice;

        private Double saleRate;

        private Integer like;

        private Double score;

        private String recentVersion;


        private List<SystemRequirement> systemRequirements;
    }

    public static class CreateRequested {
        private String thumbnailUrl;

        private List<String> imageUrls;

        private String name;

        private String description;

        private String genre;

        private Integer netPrice;

        private String recentVersion;


        private List<SystemRequirement> systemRequirements;

    }

    public static class RequestedPreview{

    }

    public static class SystemRequirement {
        private String os;
        private String minimum;
        private String recommended;
    }


    public static enum CreateRequestStatusUpdate{

    }
}
