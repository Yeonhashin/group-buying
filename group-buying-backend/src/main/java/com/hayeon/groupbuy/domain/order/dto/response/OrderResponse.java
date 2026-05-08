package com.hayeon.groupbuy.domain.order.dto.response;

import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {

    private Long orderId;

    private Long userId;

    private Long groupPurchaseId;

    private Integer totalPrice;

    private OrderStatus status;

    private String paymentId;

    private PaymentStatus paymentStatus;

    private LocalDateTime orderedDt;

    private LocalDateTime createDt;

    // ===== 생성자 =====
    public OrderResponse(
            Long orderId,
            Long userId,
            Long groupPurchaseId,
            Integer totalPrice,
            OrderStatus status,
            String paymentId,
            PaymentStatus paymentStatus,
            LocalDateTime orderedDt,
            LocalDateTime createDt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.groupPurchaseId = groupPurchaseId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.orderedDt = orderedDt;
        this.createDt = createDt;
    }

    // ===== Order 객체 기반 응답 =====
    public static OrderResponse from(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getGroupPurchaseId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getPaymentId(),
                order.getPaymentStatus(),
                order.getOrderedDt(),
                order.getCreateDt()
        );
    }

    // ===== orderId만 반환 =====
    public static OrderResponse from(Long orderId) {

        return new OrderResponse(
                orderId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}