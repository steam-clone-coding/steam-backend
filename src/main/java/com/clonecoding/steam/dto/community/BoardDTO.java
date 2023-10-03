package com.clonecoding.steam.dto.community;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.game.ReviewDTO;
import com.clonecoding.steam.entity.community.Board;
import com.clonecoding.steam.entity.community.BoardMedia;
import com.clonecoding.steam.entity.user.Country;
import com.clonecoding.steam.enums.community.BoardType;
import com.clonecoding.steam.enums.community.MediaType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BoardDTO {

    public static class Create{
        String id;
        String title;
        String ImageFileUrl; // url로 받을 수 있나?
        String description;
        String gameId;
    }
    public static class Preview{
        private Long id;
        private String mediaUrl;
        private String description;
        private Integer likeCount;
        private Integer commentCount;
        private MediaType mediaType;
        private BoardType boardType;
        private String gameUid;
        private LocalDate createDate;

        public Preview(Board board, BoardMedia boardMedia, String gameUid, Integer likeCount, Integer commentCount, LocalDate createDate) {
            this.id = board.getId();
            this.mediaUrl = boardMedia.getMediaUrl();
            this.description = board.getDescription();
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.boardType = board.getBoardType();
            this.mediaType = boardMedia.getMediaType();
            this.gameUid = gameUid;
            this.createDate = createDate;

        }
    }

    public static class Detail{
        String author;
        String title;
        String description;
        String mediaUrl;
        String gameUid;
        LocalDateTime createdAt;
    }

    public static class Comment{
        String id;
        String profileImageUrl;
        LocalDateTime lastLoginTime;
        Integer likeCount;
        String Description;
    }


}
