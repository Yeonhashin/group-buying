package com.hayeon.groupbuy.domain.order.event;

import lombok.Getter;

@Getter
public class OrderCanceledEvent {

    private final Long orderId;
    private final Long userId;

    public OrderCanceledEvent(Long orderId, Long userId) {
        this.orderId = orderId;
        this.userId = userId;
    }
}