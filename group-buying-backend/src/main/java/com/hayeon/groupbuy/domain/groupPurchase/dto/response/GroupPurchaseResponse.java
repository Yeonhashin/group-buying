package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.product.dto.response.ProductSummaryResponse;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;

import java.time.*;
import java.util.List;

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

        String remainingTime,

        List<ParticipantResponse> participants,

        LocalDate startDt,
        LocalDate endDt,
        LocalDateTime createDt,
        LocalDateTime updateDt,
        LocalDateTime deleteDt
) {

    public static GroupPurchaseResponse from(GroupPurchase groupPurchase, int currentParticipants, boolean isParticipated) {
        String remainingTime =
                calculateRemainingTime(groupPurchase);

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
                remainingTime,

                List.of(),

                groupPurchase.getStartDt(),
                groupPurchase.getEndDt(),
                groupPurchase.getCreateDt(),
                groupPurchase.getUpdateDt(),
                groupPurchase.getDeleteDt()
        );
    }

    public static GroupPurchaseResponse from(GroupPurchase groupPurchase, int currentParticipants, boolean isParticipated, List<ParticipantResponse> participants) {
        String remainingTime =
                calculateRemainingTime(groupPurchase);

        return new GroupPurchaseResponse(
                groupPurchase.getId(),
                groupPurchase.getUser().getId(),
                ProductSummaryResponse.from(groupPurchase.getProduct()),
                groupPurchase.getTitle(),
                groupPurchase.getDetails(),
                groupPurchase.getTargetPrice(),
                groupPurchase.getTargetParticipants(),
                currentParticipants,

                groupPurchase.getStatus().name(),
                isParticipated,
                remainingTime,

                participants,

                groupPurchase.getStartDt(),
                groupPurchase.getEndDt(),
                groupPurchase.getCreateDt(),
                groupPurchase.getUpdateDt(),
                groupPurchase.getDeleteDt()
        );
    }

    private static String calculateRemainingTime(GroupPurchase groupPurchase) {

        if (groupPurchase.getStatus() != GroupPurchaseStatus.RECRUITING) {
            return "마감";
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime endDateTime =
                groupPurchase.getEndDt().atTime(23, 59, 59);

        Duration duration =
                Duration.between(now, endDateTime);

        if (duration.isNegative() || duration.isZero()) {
            return "마감";
        }

        long totalHours = duration.toHours();

        long days = totalHours / 24;
        long hours = totalHours % 24;

        if (days > 2) {
            return days + "일 남음";
        }

        if (days >= 1) {
            return days + "일 " + hours + "시간 남음";
        }

        if (totalHours >= 1) {
            return totalHours + "시간 남음";
        }

        long minutes = duration.toMinutes();

        return minutes + "분 남음";
    }
}