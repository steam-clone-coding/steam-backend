package com.clonecoding.steam.dto.user;

import com.clonecoding.steam.entity.user.User;
import lombok.Builder;
import lombok.Data;

public class DeveloperDTO {

    @Data
    @Builder
    public static class Preview{
        private String id;
        private String name;

        public static Preview entityToDto(User user){
            return Preview.builder()
                    .id(user.getUid())
                    .name(user.getNickname())
                    .build();
        }
    }
}
