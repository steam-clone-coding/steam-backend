package com.clonecoding.steam.service.community;

import com.clonecoding.steam.enums.community.DeactivateType;

public interface AdminCommunityService {

    /**
     * @author: parkjunha
     * @description: 제공된 사용자 ID를 사용하여 사용자를 비활성화
     * @param: userId 비활성화할 사용자의 ID
     * @param: deactivateType 사용자를 비활성화하는 데 사용할 비활성화 타입
     * @return:
     */
    void deactivateUser(String userId, DeactivateType deactivateType);
}
