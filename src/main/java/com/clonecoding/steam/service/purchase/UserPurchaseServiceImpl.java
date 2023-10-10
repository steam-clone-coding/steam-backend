package com.clonecoding.steam.service.purchase;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.dto.order.OrderDTO;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.purchase.CartRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPurchaseServiceImpl implements UserPurchaseService{

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional
    public void addCart(String userUid, String gameUid) {
        User user = userRepository.findUserByUid(userUid)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }


    @Override
    public PaginationListDto<CartDTO.Preview> getCartList(String userUid, Pageable page) {

        return null;
    }


    @Override
    public void deleteCart(String userUid, String cartUid) {

    }

    @Override
    public OrderDTO.Detail getOrder(String orderUid, String userUid) {
        return null;
    }

    @Override
    public PaginationListDto<OrderDTO.Preview> getOrderList(String userUid, Pageable page) {
        return null;
    }

    @Override
    public void refund(String userUid, String orderUid) {

    }

    @Override
    public void order(OrderDTO.OrderRequest info) {

    }
}
