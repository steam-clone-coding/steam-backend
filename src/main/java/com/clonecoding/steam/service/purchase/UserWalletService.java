package com.clonecoding.steam.service.purchase;

import com.clonecoding.steam.dto.order.OrderDTO;
import com.clonecoding.steam.dto.user.UserDTO;
import com.clonecoding.steam.exceptions.BadRequestException;

public interface UserWalletService {


    /**
     * @description 사용자의 UserWallet을 생성하는 메서드
     * @author minseok kim
     * @param userDto UserWallet을 생성하고자 하는 유저의 정보
     * @throws BadRequestException
    */
    void createUserWallet(UserDTO.Preview userDto) throws BadRequestException;


    /**
     * @description 결제를 UserWallet으로 수행하는 메서드
     * @author minseok kim
     * @param userDto 주문자의 정보
     * @param orderRequest 주문 정보
     * @throws
    */
    void payForUserWallet(UserDTO.Preview userDto, OrderDTO.OrderRequest orderRequest);

}
