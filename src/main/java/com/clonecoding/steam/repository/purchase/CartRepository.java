package com.clonecoding.steam.repository.purchase;

import com.clonecoding.steam.entity.purchase.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 유저의 카트 리스트를 조회하는 메서드
    Page<Cart> findByUser_Uid(String userUid, Pageable pageable);

    // 유저의 특정 게임을 카트에 추가하는 메서드
    Cart save(Cart cart);


    // 유저의 특정 카트 항목을 제거하는 메서드
    void deleteByUser_UidAndUid(String userUid, String cartUid);
}
