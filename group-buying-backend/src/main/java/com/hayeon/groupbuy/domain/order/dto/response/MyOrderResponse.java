package com.hayeon.groupbuy.domain.order.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class MyOrderResponse {

    private Long groupPurchaseId;

    private String title;

    private String productName;

    private GroupPurchaseStatus groupPurchaseStatus;

    private Long orderId;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private Integer totalPrice;

    private Integer targetParticipants;

    private LocalDateTime orderCreateDt;

    private LocalDateTime participationDt;

    private LocalDateTime paidDt;

    private ParticipationStatus participationStatus;

    public MyOrderResponse(
            Long groupPurchaseId,
            String title,
            String productName,
            GroupPurchaseStatus groupPurchaseStatus,
            Long orderId,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            ParticipationStatus participationStatus,
            LocalDateTime orderCreateDt,
            Integer totalPrice,
            Integer targetParticipants,
            LocalDateTime participationDt,
            LocalDateTime paidDt
    ) {
        this.groupPurchaseId = groupPurchaseId;
        this.title = title;
        this.productName = productName;
        this.groupPurchaseStatus = groupPurchaseStatus;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.participationStatus = participationStatus;
        this.orderCreateDt = orderCreateDt;
        this.totalPrice = totalPrice;
        this.targetParticipants = targetParticipants;
        this.participationDt = participationDt;
        this.paidDt = paidDt;
    }
}