package com.hayeon.groupbuy.domain.notification.entity;

import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(length = 500)
    private String message;

    @Column(name="is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name="create_dt")
    private LocalDateTime createDt;

    // ===== 생성 메서드 =====
    public static Notification create(Long userId, NotificationStatus status, String message) {

        Notification notification = new Notification();
        notification.userId = userId;
        notification.status = status;
        notification.message = message;
        notification.isRead = false;
        notification.createDt = LocalDateTime.now();

        return notification;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}