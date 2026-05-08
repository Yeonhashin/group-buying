package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;

import java.time.LocalDate;

public record GroupPurchaseEditResponse(
        Long id,
        Long productId,
        String title,
        String details,
        Integer targetPrice,
        Integer targetParticipants,
        LocalDate startDt,
        LocalDate endDt
) {
    public static GroupPurchaseEditResponse from(GroupPurchase gp) {
        return new GroupPurchaseEditResponse(
                gp.getId(),
                gp.getProduct().getId(),
                gp.getTitle(),
                gp.getDetails(),
                gp.getTargetPrice(),
                gp.getTargetParticipants(),
                gp.getStartDt(),
                gp.getEndDt()
        );
    }
}