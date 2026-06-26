package com.hayeon.groupbuy.domain.order.event;

import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.order.service.OrderService;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupPurchaseOrderListener {

    private static final Logger log =
            LoggerFactory.getLogger(GroupPurchaseOrderListener.class);
    private final OrderService orderService;
    private final NotificationService notificationService;
    private final GroupPurchaseParticipationRepository participationRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(GroupPurchaseClosedEvent event) {

        if (!event.isSuccess()) {
            return;
        }

        log.info("GroupPurchaseOrderListener 시작 gpId={}", event.getGroupPurchaseId());

        Long gpId = event.getGroupPurchaseId();

        GroupPurchase gp = groupPurchaseRepository.findById(gpId)
                .orElseThrow(() -> new IllegalArgumentException("GROUP_PURCHASE_NOT_FOUND"));

        String title = gp.getTitle();

        List<GroupPurchaseParticipation> participants =
                participationRepository.findByGroupPurchaseIdAndStatus(
                        gpId,
                        ParticipationStatus.ACTIVE
                );

        for (GroupPurchaseParticipation p : participants) {

            Long userId = p.getUser().getId();

            try {
                Long orderId = orderService.createOrder(userId, gpId);

                log.info("주문 생성 완료 orderId={}, userId={}, gpId={}", orderId, userId, gpId);

                // 주문 생성 성공 후 알림 발송
                notificationService.createOrderCreated(userId, title);
                notificationService.create(
                        userId,
                        NotificationStatus.GROUP_PURCHASE_SUCCESS,
                        "[" + title + "] 공동구매가 목표인원 달성을 성공하였습니다."
                );

            } catch (IllegalStateException e) {
                log.warn("이미 주문 존재 userId={}, gpId={}", userId, gpId);
                // 이미 주문 있으면 알림 발송 안 함
            }
        }

        log.info("Order + Notification 처리 완료 groupPurchaseId={}", gpId);
    }
}