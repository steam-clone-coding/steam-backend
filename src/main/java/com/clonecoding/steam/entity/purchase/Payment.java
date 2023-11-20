package com.clonecoding.steam.entity.purchase;

import com.clonecoding.steam.entity.user.UserWallet;
import com.clonecoding.steam.enums.purchase.PayMethod;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class Payment {

    @Id
    private String tno;

    private PayMethod payMethod; //응답결제수단


    private Integer amount;

    private String cardName; // 결제 건의 발급 사 명
    private String cardNo; // 결제 건의 카드번호 (카드번호 16자리 중 3번째구간은 마스킹)
    private String appNo; //결제 건의 승인번호
    private LocalDateTime appTime; // 결제 건의 결제(승인) 시간 -> 데이터는 Long이므로 변환할 필요가 있음

    private Boolean noinf; // 결제 건의 무이자 여부

    /*
        noinf = Y 일 때 (무이자 결제인 경우)
        카드사 이벤트 무이자인 경우 : CARD
        상점 부담 무이자인 경우 : SHOP
    */
    private String noinfType;
    private Integer quota; // 결제 건의 할부 기간
    private Integer cardMoney; // 결제 건의 총 결제금액 중 신용카드 결제금액
    private Integer couponMoney; // 결제 건의 무이자 여부결제 건의 쿠폰 할인, 페이코 포인트 사용 금액
    private Boolean partCancel; // 결제 건의 부분취소 가능 유무
    private String ispIssuerName; //ISP 계열 카드 발급 사 명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private UserWallet userWallet;

}
