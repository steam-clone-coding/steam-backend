package com.clonecoding.steam.dto.user;

import com.clonecoding.steam.entity.user.Country;
import com.clonecoding.steam.entity.user.User;

import java.time.LocalDateTime;

public class UserDTO {

    public static class Preview{
        private String id;
        private String nickname;

        public Preview(User user) {
            this.id = user.getUid();
            this.nickname = user.getNickname();
        }
    }

    public static class Search{
        private String id;
        private String profileImageUrl;
        private String name;
        private String shortDescription;
        private Country country;
        private Boolean isFriend;
    }

    public static class Profile{
        private String id;
        private String name;
        private Country country;
        private String introduction;
        private Integer screenshotCounts;
        private Integer recommendedCounts;
        private Integer artWorkCounts;

    }

    public static class GuestBook{
        private String id;
        private String profileImageUrl;
        private LocalDateTime lastLoginTime;
        private Integer likeCount;
        private String description;
    }
}
