package com.hayeon.groupbuy.domain.order.event;

import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.order.service.OrderService;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupPurchaseOrderListener {

    private static final Logger log =
            LoggerFactory.getLogger(GroupPurchaseOrderListener.class);
    private final OrderService orderService;
    private final NotificationService notificationService;
    private final GroupPurchaseParticipationRepository participationRepository;

    @EventListener
    @Transactional
    public void handle(GroupPurchaseClosedEvent event) {

        if (!event.isSuccess()) {
            return;
        }

        Long gpId = event.getGroupPurchaseId();

        List<GroupPurchaseParticipation> participants =
                participationRepository.findByGroupPurchaseIdAndStatus(
                        gpId,
                        ParticipationStatus.ACTIVE
                );

        for (GroupPurchaseParticipation p : participants) {

            Long userId = p.getUser().getId();

            Long orderId = orderService.createOrder(userId, gpId);

            notificationService.createOrderCreated(userId, orderId);
        }

        log.info("Order + Notification 처리 완료 groupPurchaseId={}", gpId);
    }
}