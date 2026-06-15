package com.hayeon.groupbuy.domain.notification.dto.response;

import com.hayeon.groupbuy.domain.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String status,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getStatus().name(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreateDt()
        );
    }
}