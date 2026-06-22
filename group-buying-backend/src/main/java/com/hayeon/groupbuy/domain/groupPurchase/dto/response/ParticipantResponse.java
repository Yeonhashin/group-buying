package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;

public record ParticipantResponse(
        Long userId,
        String username
) {
    public static ParticipantResponse from(GroupPurchaseParticipation participation) {
        return new ParticipantResponse(
                participation.getUser().getId(),
                participation.getUser().getNickname()
        );
    }
}
