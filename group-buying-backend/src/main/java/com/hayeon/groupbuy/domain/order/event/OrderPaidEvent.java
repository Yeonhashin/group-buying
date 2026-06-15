package com.hayeon.groupbuy.domain.order.event;

public class OrderPaidEvent {

    private final Long orderId;
    private final Long userId;
    private final String groupPurchaseTitle;

    public OrderPaidEvent(
            Long orderId,
            Long userId,
            String groupPurchaseTitle
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.groupPurchaseTitle = groupPurchaseTitle;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getGroupPurchaseTitle() {
        return groupPurchaseTitle;
    }
}