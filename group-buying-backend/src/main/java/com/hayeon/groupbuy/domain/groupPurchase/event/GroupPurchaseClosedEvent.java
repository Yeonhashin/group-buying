package com.hayeon.groupbuy.domain.groupPurchase.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupPurchaseClosedEvent {

    private Long groupPurchaseId;
    private boolean success;
}