package com.hayeon.groupbuy.domain.groupPurchase.event.listener;

import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupPurchaseEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(GroupPurchaseEventListener.class);

    private final GroupPurchaseParticipationRepository participationRepository;
    private final OrderService orderService;
    private final NotificationService notificationService;

    @EventListener
    @Transactional
    public void handleGroupPurchaseClosed(GroupPurchaseClosedEvent event) {

        Long gpId = event.getGroupPurchaseId();
        boolean isSuccess = event.isSuccess();

        // 실패면 주문 생성 안함
        if (!isSuccess) {
            log.info("공동구매 실패 - 주문 생성 없음 gpId={}", gpId);
            return;
        }

        // ACTIVE 참여자 조회
        List<GroupPurchaseParticipation> participants =
                participationRepository.findByGroupPurchaseIdAndStatus(
                        gpId,
                        ParticipationStatus.ACTIVE
                );

        // 주문 생성
        for (GroupPurchaseParticipation p : participants) {

            Long userId = p.getUser().getId();

            Long orderId = orderService.createOrder(
                    userId,
                    gpId
            );

            // 알림 생성
            notificationService.createOrderCreated(
                    userId,
                    orderId
            );
        }

        log.info("공동구매 완료 → 주문 생성 완료 gpId={}, count={}", gpId, participants.size());
    }
}