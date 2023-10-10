package com.clonecoding.steam.repository.purchase;

import com.clonecoding.steam.entity.purchase.Order;
import com.clonecoding.steam.enums.purchase.PurchaseStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 주문 관련 리포지토리
 * Author: Jinyeon Seol
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 사용자 UID와 주문 코드로 주문 상세 정보를 조회
     *
     * @param userUid   사용자 UID
     * @param orderCode 주문 코드
     * @return 주문 정보 (Optional)
     */
    Optional<Order> findByUser_UidAndOrderCode(String userUid, String orderCode);

    /**
     * 사용자 ID에 따라 주문 목록을 주문일자 기준으로 내림차순으로 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이지 정보
     * @return 주문 목록 (페이지네이션 적용)
     */
    Page<Order> findByUser_IdOrderByOrderedAtDesc(Long userId, Pageable pageable);

    /**
     * 주문 환불 처리를 수행 (purchaseStatus를 REFUND로 업데이트)
     *
     * @param userUid   사용자 UID
     * @param orderCode 주문 코드
     */
    @Transactional
    default void refundOrderByUidAndOrderCode(String userUid, String orderCode) {
        Optional<Order> orderOptional = findByUser_UidAndOrderCode(userUid, orderCode);
        orderOptional.ifPresent(order -> updatePurchaseStatusByOrderCode(order.getOrderCode(), PurchaseStatus.REFUND));
    }

    /**
     * 주문 상태를 업데이트
     *
     * @param orderCode      주문 코드
     * @param purchaseStatus 업데이트할 주문 상태
     */
    @Transactional
    void updatePurchaseStatusByOrderCode(String orderCode, PurchaseStatus purchaseStatus);

    /**
     * 주문 저장
     *
     * @param order 주문 정보
     * @return 저장된 주문 정보
     */
    Order save(Order order);
}
