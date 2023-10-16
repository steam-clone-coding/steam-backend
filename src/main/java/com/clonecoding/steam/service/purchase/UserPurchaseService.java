package com.clonecoding.steam.service.purchase;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.dto.order.OrderDTO;
import org.springframework.data.domain.Pageable;

public interface UserPurchaseService {


    /**
     * @author minseok kim
     * @description 유저의 카트 리스트 조회
     * @param userUid 조회하려는 User의 ID
     * @return
     * @exception
    */
    PaginationListDto<CartDTO.Preview> getCartList(String userUid, Pageable page);


    /**
     * @author minseok kim
     * @description
     * @param userUid 추가하려는 User의 ID
     * @param gameUid 추가하려는 Game의 ID
     * @return
     * @exception
    */
    void addCart(String userUid, String gameUid);

    /**
     * @author minseok kim
     * @description
     * @param userUid 삭제하려는 User의 ID
     * @param cartUid 삭제하려는 Cart의 ID
     * @return
     * @exception
    */
    void deleteCart(String userUid, String cartUid);


    /**
     * @author minseok kim
     * @description 주문 상세 조회
     * @param orderCode 조회하려는 주문의 ID
     * @param userUid 조회하려는 주문의 userID
     * @return
     * @exception
    */
    OrderDTO.Detail getOrder(String orderCode, String userUid);

    /**
     * @author minseok kim
     * @description 주문 리스트 조회
     * @param userUid 조회하려는 유저의 ID
     * @param  page 페이지네이션
     * @return
     * @exception
    */
    PaginationListDto<OrderDTO.Preview> getOrderList(String userUid, Pageable page);


    /**
     * @author minseok kim
     * @description 주문 취소
     * @param userUid 취소하려는 유저의 ID
     * @param orderCode 취소하려는 주문의 ID
     * @return
     * @exception
    */
    void refund(String userUid, String orderCode);


    /**
     * @author minseok kim
     * @description 주문
     * @param info 주문시 필요한 정보
     * @return
     * @exception
    */
    void order(OrderDTO.OrderRequest info);
}
