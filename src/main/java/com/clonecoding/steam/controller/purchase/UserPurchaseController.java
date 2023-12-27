package com.clonecoding.steam.controller.purchase;

import com.clonecoding.steam.aop.annotation.UserInfoRead;
import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.order.CartDTO;
import com.clonecoding.steam.dto.order.OrderDTO;
import com.clonecoding.steam.dto.user.UserDTO;
import com.clonecoding.steam.service.purchase.UserPurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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


    @Operation(summary = "카트에 게임을 추가하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 추가 성공시"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 불가할 시"),
            @ApiResponse(responseCode = "404", description = "게임 ID조회가 불가능 할 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 조회 또는 추가 실패시")
    })
    @PostMapping("/order/cart/{gameUid}")
    @UserInfoRead
    public ResponseEntity<String> addToCart(@PathVariable String gameUid, HttpServletRequest req) {
        UserDTO.Preview user = (UserDTO.Preview) req.getAttribute("user");
        String userUid = user.getId();


        userPurchaseService.addCart(userUid, gameUid);
        return ResponseEntity.ok().build();
    }



    @Operation(summary = "사용자가 추가한 카트 리스트를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 추가 성공시"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 불가할 시"),
            @ApiResponse(responseCode = "404", description = "게임 ID조회가 불가능 할 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 조회 또는 추가 실패시")
    })
    @GetMapping("/order/cart")
    @UserInfoRead
    public ResponseEntity<PaginationListDto<CartDTO.Preview>> getCartList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest req
    ) {

            UserDTO.Preview user = (UserDTO.Preview) req.getAttribute("user");
            String userUid = user.getId();
            Pageable pageable = PageRequest.of(page - 1, size);
            return ResponseEntity.ok(userPurchaseService.getCartList(userUid, pageable));
    }

    @Operation(summary = "카트에 존재하는 게임을 삭제하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 제거 성공시"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 불가할 시"),
            @ApiResponse(responseCode = "404", description = "게임 ID조회가 불가능 할 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 조회 또는 제거 실패시")
    })
    @DeleteMapping("/order/cart/{cartUid}")
    @UserInfoRead
    public ResponseEntity<String> deleteFromCart(
            @RequestParam String cartUid,
            HttpServletRequest req
    ) {
        UserDTO.Preview user = (UserDTO.Preview) req.getAttribute("user");
        String userUid = user.getId();
        userPurchaseService.deleteCart(userUid, cartUid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Item removed from cart successfully");
    }

    @Operation(summary = "사용자의 주문내역 상세 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 조회 성공시"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 불가할 시"),
            @ApiResponse(responseCode = "404", description = "주문 ID조회가 불가능 할 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 조회 실패시")
    })
    @GetMapping("/order/{orderCode}")
    @UserInfoRead
    public ResponseEntity<OrderDTO.Detail> getOrder(
            @PathVariable String orderCode,
            HttpServletRequest req
    ) {
        UserDTO.Preview user = (UserDTO.Preview) req.getAttribute("user");
        String userUid = user.getId();
        return ResponseEntity.ok(userPurchaseService.getOrder(orderCode, userUid));
    }


    @Operation(summary = "사용자의 주문내역 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 추가 성공시"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 불가할 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 조회 또는 추가 실패시")
    })
    @GetMapping("/order")
    @UserInfoRead
    public ResponseEntity<PaginationListDto<OrderDTO.Preview>> getOrderList(
            Pageable page,
            HttpServletRequest req
    ) {
        UserDTO.Preview user = (UserDTO.Preview) req.getAttribute("user");
        String userUid = user.getId();
        return ResponseEntity.ok(userPurchaseService.getOrderList(userUid, page));
    }


    @Operation(summary = "사용자의 주문 정보를 저장하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 조회 성공시"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 불가할 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 조회 실패시")
    })
    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDTO.OrderRequest orderRequest) {
        userPurchaseService.order(orderRequest);
        return ResponseEntity.ok().build();
    }
}
