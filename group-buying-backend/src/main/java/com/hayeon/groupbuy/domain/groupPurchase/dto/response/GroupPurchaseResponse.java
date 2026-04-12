package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.product.dto.response.ProductSummaryResponse;

import java.time.*;

public record GroupPurchaseResponse(
        Long id,
        Long userId,
        ProductSummaryResponse product,
        String title,
        String details,
        Integer targetPrice,
        Integer targetParticipants,
        Integer currentParticipants,
        LocalDate startDt,
        LocalDate endDt,
        LocalDateTime createDt,
        LocalDateTime updateDt,
        LocalDateTime deleteDt
) {

    public static GroupPurchaseResponse from(GroupPurchase groupPurchase) {
        return new GroupPurchaseResponse(
                groupPurchase.getId(),
                groupPurchase.getUser().getId(),
                ProductSummaryResponse.from(groupPurchase.getProduct()),
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