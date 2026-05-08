package com.hayeon.groupbuy.domain.order.enums;

public enum PaymentStatus {
    READY,     // 결제 대기
    PAID,      // 결제 완료
    FAILED,    // 결제 실패
    REFUNDED   // 환불 완료
}