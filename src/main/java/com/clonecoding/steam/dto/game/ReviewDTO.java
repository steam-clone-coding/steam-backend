package com.clonecoding.steam.dto.game;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.user.UserDTO;
import com.clonecoding.steam.entity.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDTO {

    public static class PaginationList extends PaginationListDto<Detail> {

        private OverallReviews overallReviews;

//        @Builder
        public PaginationList(Number count, List<Detail> data, OverallReviews overallReviews) {
            super(count, data);
            this.overallReviews = overallReviews;
        }
    }


    @Data
    public static class Detail{
        private UserDTO.Preview user;
        private boolean recommend;
        private String image;
        private String content;
        private HelpfulCount helpfulCount;
        private boolean isUserLiked;
        private LocalDateTime writeTime;

        public Detail(User user, boolean recommend, String image, String content, HelpfulCount helpfulCount, boolean isUserLiked, LocalDateTime writeTime) {
            this.user = new UserDTO.Preview(user);
            this.recommend = recommend;
            this.image = image;
            this.content = content;
            this.helpfulCount = helpfulCount;
            this.isUserLiked = isUserLiked;
            this.writeTime = writeTime;
        }
    }

    public static enum OverallReviews{
        VERY_POSITIVE, POSITIVE, NEGATIVE, VERY_NEGATIVE
    }

    @Data
    @Builder
    public static class HelpfulCount{
        private Integer like;
        private Integer unlike;
    }
}
