package com.hayeon.groupbuy.domain.notification.event;

import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.order.event.OrderPaidEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseNotificationListener {

    private final GroupPurchaseParticipationRepository participationRepository;
    private final NotificationService notificationService;
    private final GroupPurchaseRepository groupPurchaseRepository;

    @EventListener
    public void handle(GroupPurchaseClosedEvent event) {

        // 성공 알림은 GroupPurchaseOrderListener에서 처리
        if (event.isSuccess()) return;

        Long gpId = event.getGroupPurchaseId();

        GroupPurchase gp = groupPurchaseRepository.findById(gpId)
                .orElseThrow(() -> new IllegalArgumentException("GROUP_PURCHASE_NOT_FOUND"));

        String title = gp.getTitle();

        List<GroupPurchaseParticipation> participants =
                participationRepository.findByGroupPurchaseIdAndStatus(
                        gpId,
                        ParticipationStatus.ACTIVE
                );

        for (GroupPurchaseParticipation participation : participants) {
            Long userId = participation.getUser().getId();
            notificationService.create(
                    userId,
                    NotificationStatus.GROUP_PURCHASE_FAILED,
                    "[" + title + "] 공동구매가 목표인원 달성을 실패했습니다."
            );
        }

        log.info("공동구매 실패 알림 생성 완료 id={}", gpId);
    }

    @EventListener
    public void handle(OrderPaidEvent event) {
        notificationService.createOrderPaid(
                event.getUserId(),
                event.getGroupPurchaseTitle()
        );
        log.info("결제 완료 알림 생성 orderId={}", event.getOrderId());
    }
}