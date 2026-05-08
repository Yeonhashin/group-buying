package com.hayeon.groupbuy.domain.notification.enums;

public enum NotificationStatus {

    ORDER_CREATED,        // 주문 생성
    ORDER_PAID,           // 결제 완료
    ORDER_FAILED,         // 결제 실패
    ORDER_AUTO_CANCELED,  // 미결제 자동 취소
    ORDER_REFUNDED        // 환불 완료

}