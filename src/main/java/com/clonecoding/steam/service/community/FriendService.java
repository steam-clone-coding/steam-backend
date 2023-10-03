package com.clonecoding.steam.service.community;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.community.FriendDTO;
import org.springframework.data.domain.Pageable;

public interface FriendService {

    /**
     * @author: parkjunha
     * @description: 제공된 사용자 ID를 기반으로 사용자의 친구 정보 리스트 조회
     * @param: userId 친구 정보를 검색할 사용자의 ID
     * @param page 페이지네이션
     * @return: 주어진 사용자 ID와 페이지 정보에 따라 검색된 친구 정보 목록을 포함하는 PaginationListDto 객체
     */
    PaginationListDto<FriendDTO.Preview> getUserFriend(String userId, Pageable page);

    /**
     * @author: parkjunha
     * @description: 친구 요청
     * @param: userId 친구 요청을 보내는 사용자의 ID
     * @param: friendId 친구 요청을 받는 사용자의 ID
     * @return:
     */
    void requestFriendship(String userId, String friendId);

    /**
     * @author: parkjunha
     * @description: 친구 삭제
     * @param: userId 친구를 삭제하려는 사용자의 ID
     * @param: friendId 삭제할 친구의 ID
     * @return:
     */
    void removeFriend(String userId, String friendId);


}
