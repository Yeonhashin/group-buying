package com.hayeon.groupbuy.domain.order.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import lombok.Getter;

@Getter
public class MyOrderResponse {

    private Long groupPurchaseId;
    private String title;
    private GroupPurchaseStatus groupPurchaseStatus;
    private Long orderId;
    private OrderStatus orderStatus;
    private Integer totalPrice;

    public MyOrderResponse(
            Long groupPurchaseId,
            String title,
            GroupPurchaseStatus groupPurchaseStatus,
            Long orderId,
            OrderStatus orderStatus,
            Integer totalPrice
    ) {
        this.groupPurchaseId = groupPurchaseId;
        this.title = title;
        this.groupPurchaseStatus = groupPurchaseStatus;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }
}