package com.clonecoding.steam.service.order;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.dto.order.OrderDTO;
import org.springframework.data.domain.Pageable;

public interface UserOrderService {


    /**
     * @author minseok kim
     * @description 유저의 카트 리스트 조회
     * @param userId 조회하려는 User의 ID
     * @return
     * @exception
    */
    PaginationListDto<CartDTO.Preview> getCartList(String userId, Pageable page);


    /**
     * @author minseok kim
     * @description
     * @param userId 추가하려는 User의 ID
     * @param gameId 추가하려는 Game의 ID
     * @return
     * @exception
    */
    void addCart(String userId, String gameId);

    /**
     * @author minseok kim
     * @description
     * @param userId 삭제하려는 User의 ID
     * @param cartId 삭제하려는 Cart의 ID
     * @return
     * @exception
    */
    void deleteCart(String userId, String cartId);


    /**
     * @author minseok kim
     * @description 주문 상세 조회
     * @param orderId 조회하려는 주문의 ID
     * @param userId 조회하려는 주문의 userID
     * @return
     * @exception
    */
    OrderDTO.Detail getOrder(String orderId, String userId);

    /**
     * @author minseok kim
     * @description 주문 리스트 조회
     * @param userId 조회하려는 유저의 ID
     * @param  page 페이지네이션
     * @return
     * @exception
    */
    PaginationListDto<OrderDTO.Preview> getOrderList(String userId, Pageable page);


    /**
     * @author minseok kim
     * @description 주문 취소
     * @param userId 취소하려는 유저의 ID
     * @param orderId 취소하려는 주문의 ID
     * @return
     * @exception
    */
    void refund(String userId, String orderId);


    /**
     * @author minseok kim
     * @description 주문
     * @param info 주문시 필요한 정보
     * @return
     * @exception
    */
    void order(OrderDTO.OrderRequest info);
}
