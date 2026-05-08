package com.hayeon.groupbuy.domain.notification.service;

import com.hayeon.groupbuy.domain.notification.entity.Notification;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ===== 공통 생성 (내부 전용) =====
    @Transactional
    public void create(Long userId, NotificationStatus status, String message) {

        Notification notification =
                Notification.create(userId, status, message);

        notificationRepository.save(notification);
    }

    // ===== 주문 생성 =====
    @Transactional
    public void createOrderCreated(Long userId, Long orderId) {
        create(
                userId,
                NotificationStatus.ORDER_CREATED,
                "주문이 생성되었습니다. 주문번호: " + orderId
        );
    }

    // ===== 결제 완료 =====
    @Transactional
    public void createOrderPaid(Long userId, Long orderId) {
        create(
                userId,
                NotificationStatus.ORDER_PAID,
                "결제가 완료되었습니다. 주문번호: " + orderId
        );
    }

    // ===== 결제 실패 =====
    @Transactional
    public void createOrderFailed(Long userId, Long orderId) {
        create(
                userId,
                NotificationStatus.ORDER_FAILED,
                "결제에 실패했습니다. 다시 시도해주세요. 주문번호: " + orderId
        );
    }

    // ===== 자동 취소 =====
    @Transactional
    public void createOrderCanceled(Long userId, Long orderId) {
        create(
                userId,
                NotificationStatus.ORDER_AUTO_CANCELED,
                "미결제로 주문이 자동 취소되었습니다. 주문번호: " + orderId
        );
    }

    // ===== 환불 =====
    @Transactional
    public void createOrderRefunded(Long userId, Long orderId) {
        create(
                userId,
                NotificationStatus.ORDER_REFUNDED,
                "환불이 완료되었습니다. 주문번호: " + orderId
        );
    }

    // ===== 조회 =====
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreateDtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false);
    }

    // ===== 읽음 처리 =====
    @Transactional
    public void markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        notification.markAsRead();
    }
}