package com.clonecoding.steam.controller.purchase;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.dto.order.OrderDTO;
import com.clonecoding.steam.service.purchase.UserPurchaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserPurchaseController {
    // TODO: userId 처리 필요, 테스트 코드 필요

    private final UserPurchaseService userPurchaseService;

    public UserPurchaseController(UserPurchaseService userPurchaseServiceImp) {
        this.userPurchaseService = userPurchaseServiceImp;
    }

    @PostMapping("/order/cart/{gameUid}")
    public ResponseEntity<String> addToCart(@PathVariable String gameUid) {

        userPurchaseService.addCart(userUid, gameUid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/cart")
    public ResponseEntity<PaginationListDto<CartDTO.Preview>> getCartList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
            Pageable pageable = PageRequest.of(page - 1, size);
            return ResponseEntity.ok(userPurchaseService.getCartList(userUid, pageable));
    }

    @DeleteMapping("/order/cart/{cartUid}")
    public ResponseEntity<String> deleteFromCart(@RequestParam String cartUid) {
        userPurchaseService.deleteCart(userUid, cartUid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item removed from cart successfully");
    }

    @GetMapping("/order/{orderCode}")
    public ResponseEntity<OrderDTO.Detail> getOrder(@PathVariable String orderCode) {
        return ResponseEntity.ok(userPurchaseService.getOrder(orderCode, userUid));
    }

    @GetMapping("/order")
    public ResponseEntity<PaginationListDto<OrderDTO.Preview>> getOrderList(Pageable page) {
        return ResponseEntity.ok(userPurchaseService.getOrderList(userUid, page));
    }

    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDTO.OrderRequest orderRequest) {
        userPurchaseService.order(orderRequest);
        return ResponseEntity.ok().build();
    }
}
