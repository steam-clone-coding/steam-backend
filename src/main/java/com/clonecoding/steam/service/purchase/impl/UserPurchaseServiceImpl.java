package com.clonecoding.steam.service.purchase.impl;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.dto.order.OrderDTO;
import com.clonecoding.steam.entity.game.Game;
import com.clonecoding.steam.entity.purchase.Cart;
import com.clonecoding.steam.entity.purchase.Order;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.game.GameRepository;
import com.clonecoding.steam.repository.purchase.CartRepository;
import com.clonecoding.steam.repository.purchase.OrderRepository;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.service.purchase.UserPurchaseService;
import com.clonecoding.steam.utils.common.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPurchaseServiceImpl implements UserPurchaseService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final NanoIdProvider nanoIdProvider;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void addCart(String userUid, String gameUid) {
        User user = userRepository.findUserByUid(userUid)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.USER_NOT_FOUND.getMessage()));


        Game game = gameRepository.findByUid(gameUid)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.GAME_NOT_FOUND.getMessage()));


        boolean isDuplicatedGame = cartRepository.findByUser_Uid(userUid, Pageable.unpaged())
                .stream().anyMatch(c -> Objects.equals(c.getGame().getUid(), gameUid));

        if(isDuplicatedGame){
            throw new IllegalArgumentException(ExceptionMessages.ALREADY_IN_CART.getMessage());
        }

        Cart cart = Cart.builder()
                .user(user)
                .game(game)
                .uid(nanoIdProvider.createNanoId())
                .build();

        cartRepository.save(cart);

    }


    @Override
    public PaginationListDto<CartDTO.Preview> getCartList(String userUid, Pageable page) {

        Page<Cart> result = cartRepository.findByUser_Uid(userUid, page);

        return PaginationListDto.<CartDTO.Preview>builder()
                .count(result.getTotalElements())
                .data(result.getContent().stream().map(CartDTO.Preview::entityToDto).toList())
                .build();
    }


    @Override
    @Transactional
    public void deleteCart(String userUid, String cartUid) {
        cartRepository.deleteByUser_UidAndUid(userUid, cartUid);
    }

    @Override
    public OrderDTO.Detail getOrder(String orderCode, String userUid) {
        Order order = orderRepository.findByUser_UidAndOrderCode(userUid, orderCode)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.ORDER_NOT_FOUND.getMessage()));

        // TODO : entityToDto 구현
        return OrderDTO.Detail.entityToDto(order);

    }

    @Override
    public PaginationListDto<OrderDTO.Preview> getOrderList(String userUid, Pageable page) {
        User user = userRepository.findUserByUid(userUid)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.USER_NOT_FOUND.getMessage()));

        Page<Order> orders = orderRepository.findByUser_IdOrderByOrderedAtDesc(user.getId(), page);


        // TODO : entityToDto 구현
        return PaginationListDto.<OrderDTO.Preview>builder()
                .count(orders.getTotalElements())
                .data(orders.getContent().stream().map(OrderDTO.Preview::entityToDto).toList())
                .build();


    }

    @Override
    @Transactional
    public void refund(String userUid, String orderCode) {

    }

    @Override
    public void order(OrderDTO.OrderRequest info) {

    }
}
