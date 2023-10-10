package com.clonecoding.steam.repository.purchase;

import com.clonecoding.steam.entity.purchase.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 카트 관련 리포지토리
 * Author: Jinyeong Seol
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * 사용자 UID와 페이지 정보를 이용하여 카트 리스트를 조회
     *
     * @param userUid  사용자 UID
     * @param pageable 페이지 정보
     * @return 카트 리스트 (페이지네이션 적용)
     */
    Page<Cart> findByUser_Uid(String userUid, Pageable pageable);

    /**
     * 특정 게임이 사용자의 카트에 존재하는지 확인
     *
     * @param gameUid  게임 UID
     * @param userUid  사용자 UID
     * @return 해당 게임이 사용자의 카트에 존재하면 true, 그렇지 않으면 false 반환
     */
    // TODO: 테스트 필요!! 정상적으로 작동하지 않을 수 있음
    boolean existsByGame_UidAndUser_Uid(String gameUid, String userUid);

    /**
     * 사용자의 카트에 특정 항목을 추가
     *
     * @param cart 카트 항목
     * @return 추가된 카트 항목 정보
     */
    Cart save(Cart cart);

    /**
     * 사용자의 카트에서 특정 카트 항목을 제거
     *
     * @param userUid 사용자 UID
     * @param cartUid 카트 항목 UID
     */
    void deleteByUser_UidAndUid(String userUid, String cartUid);
}
