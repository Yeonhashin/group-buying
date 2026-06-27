package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import java.time.LocalDate;

public record SellerGroupPurchaseResponse(
        Long id,
        String title,
        String status,
        LocalDate startDt,
        LocalDate endDt,
        Integer targetParticipants,
        Integer currentParticipants
) {
    public static SellerGroupPurchaseResponse from(GroupPurchase gp, int currentParticipants) {
        return new SellerGroupPurchaseResponse(
                gp.getId(),
                gp.getTitle(),
                gp.getStatus().name(),
                gp.getStartDt(),
                gp.getEndDt(),
                gp.getTargetParticipants(),
                currentParticipants
        );
    }
}