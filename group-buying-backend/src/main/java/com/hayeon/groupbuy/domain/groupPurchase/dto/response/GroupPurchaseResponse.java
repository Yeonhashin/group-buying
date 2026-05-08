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

        // 공동 구매 상세 > 참여 여부 확인
        String status,
        Boolean isParticipated,

        LocalDate startDt,
        LocalDate endDt,
        LocalDateTime createDt,
        LocalDateTime updateDt,
        LocalDateTime deleteDt
) {

    public static GroupPurchaseResponse from(GroupPurchase groupPurchase, int currentParticipants, boolean isParticipated) {
        return new GroupPurchaseResponse(
                groupPurchase.getId(),
                groupPurchase.getUser().getId(),
                ProductSummaryResponse.from(groupPurchase.getProduct()),
                groupPurchase.getTitle(),
                groupPurchase.getDetails(),
                groupPurchase.getTargetPrice(),
                groupPurchase.getTargetParticipants(),
                currentParticipants,

                // 공동 구매 참여 여부 확인용
                groupPurchase.getStatus().name(),
                isParticipated,

                groupPurchase.getStartDt(),
                groupPurchase.getEndDt(),
                groupPurchase.getCreateDt(),
                groupPurchase.getUpdateDt(),
                groupPurchase.getDeleteDt()
        );
    }
}