package com.hayeon.groupbuy.domain.notification.controller;

import com.hayeon.groupbuy.domain.notification.dto.response.NotificationResponse;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ===== 전체 조회 =====
    @GetMapping
    public List<NotificationResponse> getNotifications(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return notificationService.getNotifications(user.getId());
    }

    // ===== 안읽은 알림 =====
    @GetMapping("/unread")
    public List<NotificationResponse> getUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return notificationService.getUnreadNotifications(user.getId());
    }

    // ===== 읽음 처리 =====
    @PatchMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        notificationService.markAsRead(notificationId, user.getId());
    }

    // ===== 모두 읽음 처리 =====
    @PatchMapping("/read-all")
    public void markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        notificationService.markAllAsRead(
                user.getId()
        );
    }
}