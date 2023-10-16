package com.clonecoding.steam.dto.game;

import com.clonecoding.steam.dto.user.DeveloperDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.game.GameCategory;
import com.clonecoding.steam.entity.game.GameMedia;
import com.clonecoding.steam.entity.purchase.DiscountedGame;
import com.clonecoding.steam.enums.game.GameMediaType;
import com.clonecoding.steam.enums.purchase.DiscountTypes;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class GameDTO {
    @Data
    @Builder
    public static class Search {
        private String id;
        private String thumbnailUrl;
        private String name;
        private Integer price;
        public static Search entityToDto(Game game, String gameThumbnailUrl){
            return Search.builder()
                    .id(game.getUid())
                    .thumbnailUrl(gameThumbnailUrl)
                    .name(game.getName())
                    .price(game.getPrice())
                    .build();
        }

    }


    public static class Preview{
        private String id;
        private String thumbnailUrl;
        private String name;
        private Integer netPrice;
        private Integer salePrice;
        private Double saleRate;

    }

    @Data
    @Builder
    public static class Detail{
        private String id;
        private String thumbnailUrl;

        private List<String> imageUrls;

        private String name;

        private String description;

        private List<GameCategory> genre;

        private DeveloperDTO.Preview developer;

        private LocalDate releaseDate;

        private Integer netPrice;

        private Integer salePrice;

        private Float saleRate;

        private Integer like;

        private Float score;

        private String recentVersion;

        private SystemRequirement systemRequirements;

        public static Detail entityToDto(Game game, LocalDateTime now){
            return Detail.builder()
                    .id(game.getUid())
                    .thumbnailUrl(getThumbnailUrl(game))
                    .imageUrls(getImagesUrl(game))
                    .name(game.getName())
                    .description(game.getDescription())
                    .genre(game.getGameCategories())
                    .developer(DeveloperDTO.Preview.entityToDto(game.getDeveloper()))
                    .releaseDate(game.getReleaseDate())
                    .netPrice(game.getPrice())
                    .salePrice(getSalePrice(game, now))
                    .saleRate(getSaleRate(game, now))
                    .like(game.getLikedByUsers().size())
                    .score(4.5F) // ?
                    .recentVersion(game.getRecentVersion())
                    .systemRequirements(SystemRequirement.entityToDto(game))
                    .build();
        }

        public static String getThumbnailUrl(Game game){
            return game.getGameMedias().stream()
                    .filter(media -> media.getMediaType().equals(GameMediaType.HEADER_IMAGE))
                    .findFirst()
                    .map(GameMedia::getMediaUrl)
                    .orElse("None");
        }

        public static List<String> getImagesUrl(Game game){
            return game.getGameMedias().stream()
                    .filter(media -> !media.getMediaType().equals(GameMediaType.HEADER_IMAGE))
                    .map(GameMedia::getMediaUrl)
                    .toList();
        }

        public static Float getSaleRate(Game game, LocalDateTime now) {
            return game.getDiscountedGames().stream()
                    .filter(dg -> dg.getDiscountPolicy().getDiscountType().equals(DiscountTypes.PERCENT))
                    .filter(dg -> dg.getDiscountPolicy().getEndDate().after(Timestamp.valueOf(now)))
                    .map(DiscountedGame::getRateDiscountRate)
                    .max(Float::compareTo)
                    .orElse(0.0F);
        }

        public static Integer getSalePrice(Game game, LocalDateTime now) {
            Float discountRate = getSaleRate(game, now); // getSaleRate는 앞서 제공한 메서드입니다.

            return (int) (game.getPrice() * (1 - discountRate));
        }

    }

    public static class CreateRequested {
        private String thumbnailUrl;

        private List<String> imageUrls;

        private String name;

        private String description;

        private String genre;

        private Integer netPrice;

        private String recentVersion;


        private SystemRequirement systemRequirements;

    }

    public static class RequestedPreview{

    }

    @Data
    @Builder
    public static class SystemRequirement {
        private List<String> os;
        private String minimum;
        private String recommended;

        public static SystemRequirement entityToDto(Game game) {
            if (game.getRequirements() == null) {
                return SystemRequirement.builder()
                        .os(List.of("None", "None", "None"))
                        .minimum("None")
                        .recommended("None")
                        .build();
            }

            List<String> os = List.of(
                    defaultValue(game.getRequirements().getPcRequirements()),
                    defaultValue(game.getRequirements().getLinuxRequirements()),
                    defaultValue(game.getRequirements().getMacRequirements())
            );

            String minimum = defaultValue(game.getRequirements().getMinimum());
            String recommended = defaultValue(game.getRequirements().getRecommended());

            return SystemRequirement.builder()
                    .os(os)
                    .minimum(minimum)
                    .recommended(recommended)
                    .build();
        }

        private static String defaultValue(String value) {
            return (value == null || value.isEmpty()) ? "None" : value;
        }
    }


    public static enum CreateRequestStatusUpdate{

    }
}
