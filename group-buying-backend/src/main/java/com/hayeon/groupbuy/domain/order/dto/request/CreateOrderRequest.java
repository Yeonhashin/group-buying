package com.hayeon.groupbuy.domain.order.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {
    private Long groupPurchaseId;
}