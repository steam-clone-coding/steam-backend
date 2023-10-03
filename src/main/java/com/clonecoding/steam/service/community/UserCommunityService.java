package com.clonecoding.steam.service.community;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.community.FriendDTO;
import com.clonecoding.steam.dto.user.UserDTO;
import org.springframework.data.domain.Pageable;

public interface UserCommunityService {


    /*
     * @author: parkjunha
     * @description: 이름으로 검색한 유저 리스트 조회
     * @param: userName 검색할 유저 이름
     * @param: page 페이지네이션
     * @return: 주어진 유저 이름과 페이지 정보에 따라 검색된 유저 목록을 포함하는 PaginationListDto 객체
     * 만약 검색된 유저가 없다면, 빈 PaginationListDto 객체를 반환
     */
    PaginationListDto<UserDTO.Search> searchUsers(String userName, Pageable page);

    /**
     * @author: parkjunha
     * @description: 주어진 유저 아이디에 해당하는 유저의 프로필 정보를 조회
     * @param: userId 조회할 유저의 아이디
     * @return: 주어진 유저 아이디에 해당하는 유저의 프로필 정보를 포함하는 UserDTO.Profile 객체
     */
    UserDTO.Profile getUserProfile(String userId);

    /**
     * @author: parkjunha
     * @description: 제공된 사용자 ID를 기반으로 사용자의 친구 정보 리스트 조회
     * @param: userId 친구 정보를 검색할 사용자의 ID
     * @return: 친구의 ID, 프로필 이미지 URL, 및 이름을 포함하는 FriendDTO.Preview 객체
     */
    FriendDTO.Preview getUserFriend(String userId);

    /**
     * @author: parkjunha
     * @description: 제공된 사용자 ID를 기반으로 사용자의 방명록 정보 조회
     * @param: userId 방명록 정보를 검색할 사용자의 ID
     * @param: page 페이지네이션
     * @return:
     */
    PaginationListDto<UserDTO.GuestBook> getUserGuestBook(String userId, Pageable page);


}
