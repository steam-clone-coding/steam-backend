package com.clonecoding.steam.dto.community;

import com.clonecoding.steam.enums.community.DeactivateType;

public class AdminDTO {
    public static class BlockUser{
        private String id;
        private DeactivateType deactivateType;
    }
}
