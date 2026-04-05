package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;

import java.time.LocalDateTime;

public record GroupPurchaseResponse(
        Long id,
        Long userId,
        Long productId,
        String title,
        String details,
        Integer targetPrice,
        Integer targetParticipants,
        Integer currentParticipants,
        LocalDateTime startDt,
        LocalDateTime endDt,
        LocalDateTime createDt,
        LocalDateTime updateDt,
        LocalDateTime deleteDt
) {

    public static GroupPurchaseResponse from(GroupPurchase groupPurchase) {
        return new GroupPurchaseResponse(
                groupPurchase.getId(),
                groupPurchase.getUser().getId(),
                groupPurchase.getProduct().getId(),
                groupPurchase.getTitle(),
                groupPurchase.getDetails(),
                groupPurchase.getTargetPrice(),
                groupPurchase.getTargetParticipants(),
                groupPurchase.getCurrentParticipants(),
                groupPurchase.getStartDt(),
                groupPurchase.getEndDt(),
                groupPurchase.getCreateDt(),
                groupPurchase.getUpdateDt(),
                groupPurchase.getDeleteDt()
        );
    }
}