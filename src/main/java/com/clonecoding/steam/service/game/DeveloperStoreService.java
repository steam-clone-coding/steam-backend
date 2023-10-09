package com.clonecoding.steam.service.game;

import com.clonecoding.steam.dto.game.GameDTO;

public interface DeveloperStoreService {


    /**
     * @author minseok kim
     * @description
     * @param gameInfo 게임 추가에 필요한 정보들
     * @param developerId developerID
     * @return gameId 신청 완료된 game은 gameID를 갖게됨.
     * @exception
    */
    String register(GameDTO.CreateRequested gameInfo, String developerId);

}
