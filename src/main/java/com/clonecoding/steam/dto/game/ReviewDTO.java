package com.clonecoding.steam.dto.game;

import com.clonecoding.steam.dto.PaginationListDto;
import com.clonecoding.steam.dto.user.UserDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDTO {

    public static class PaginationList extends PaginationListDto<Detail> {

        private OverallReviews overallReviews;

        @Builder
        public PaginationList(Number count, List<Detail> data, OverallReviews overallReviews) {
            super(count, data);
            this.overallReviews = overallReviews;
        }
    }

    public static class Detail{
        private UserDTO.Preview user;
        private boolean recommend;
        private String image;
        private String content;
        private HelpfulCount helpfulCount;
        private boolean isUserLiked;
        private LocalDateTime writeTime;
    }

    public static enum OverallReviews{
        VERY_POSITIVE, POSITIVE, NEGATIVE, VERY_NEGATIVE
    }

    public static class HelpfulCount{
        private Integer like;
        private Integer unlike;
    }
}
