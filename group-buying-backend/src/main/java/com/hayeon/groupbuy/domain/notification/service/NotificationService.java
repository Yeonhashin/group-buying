package com.hayeon.groupbuy.domain.notification.service;

import com.hayeon.groupbuy.domain.notification.entity.Notification;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.notification.repository.NotificationRepository;
import com.hayeon.groupbuy.domain.notification.dto.response.NotificationResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ===== 공통 생성 (내부 전용) =====
    @Transactional
    public void create(Long userId,
                       NotificationStatus status,
                       String message) {

        Notification notification =
                Notification.create(userId, status, message);

        notificationRepository.saveAndFlush(notification);
    }

    // ===== 주문 생성 =====
    @Transactional
    public void createOrderCreated(Long userId, String groupPurchaseTitle) {

        create(
                userId,
                NotificationStatus.ORDER_CREATED,
                "[" + groupPurchaseTitle + "] 주문이 생성되었습니다."
        );
    }

    // ===== 결제 완료 =====
    @Transactional
    public void createOrderPaid(
            Long userId,
            String groupPurchaseTitle
    ) {

        create(
                userId,
                NotificationStatus.ORDER_PAID,
                "[" + groupPurchaseTitle + "] 결제가 완료되었습니다."
        );
    }

    // ===== 결제 실패 =====
    @Transactional
    public void createOrderFailed(
            Long userId,
            String groupPurchaseTitle
    ) {

        create(
                userId,
                NotificationStatus.ORDER_FAILED,
                "[" + groupPurchaseTitle + "] 결제에 실패했습니다."
        );
    }

    // ===== 사용자 주문 취소 =====
    @Transactional
    public void createOrderCanceledByUser(
            Long userId,
            String groupPurchaseTitle
    ) {

        create(
                userId,
                NotificationStatus.ORDER_CANCELED,
                "[" + groupPurchaseTitle + "] 주문이 취소되었습니다."
        );
    }

    // ===== 자동 주문 취소 =====
    @Transactional
    public void createOrderAutoCanceled(
            Long userId,
            String groupPurchaseTitle
    ) {

        create(
                userId,
                NotificationStatus.ORDER_AUTO_CANCELED,
                "[" + groupPurchaseTitle + "] 미결제로 자동 취소되었습니다."
        );
    }

    // ===== 환불 =====
    @Transactional
    public void createOrderRefunded(
            Long userId,
            String groupPurchaseTitle
    ) {

        create(
                userId,
                NotificationStatus.ORDER_REFUNDED,
                "[" + groupPurchaseTitle + "] 환불이 완료되었습니다."
        );
    }

    // ===== 공동구매 참여 =====
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createParticipationJoined(Long userId, String groupPurchaseTitle) {
        create(
                userId,
                NotificationStatus.PARTICIPATION_JOINED,
                "[" + groupPurchaseTitle + "] 공동구매에 참여하였습니다."
        );
    }

    // ===== 공동구매 참여 취소 =====
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createParticipationCanceled(Long userId, String groupPurchaseTitle) {
        create(
                userId,
                NotificationStatus.PARTICIPATION_CANCELED,
                "[" + groupPurchaseTitle + "] 공동구매 참여를 취소하였습니다."
        );
    }

    // ===== 전체 조회 =====
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreateDtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    // ===== 안읽은 알림 =====
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    // ===== 단건 읽음 처리 =====
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // 보안: 본인 알림인지 체크
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalStateException("INVALID_USER");
        }

        notification.markAsRead();
    }

    // ===== 모두 읽음 처리 =====
    @Transactional
    public void markAllAsRead(Long userId) {

        List<Notification> notifications =
                notificationRepository.findByUserIdAndIsRead(
                        userId,
                        false
                );

        notifications.forEach(Notification::markAsRead);
    }
}