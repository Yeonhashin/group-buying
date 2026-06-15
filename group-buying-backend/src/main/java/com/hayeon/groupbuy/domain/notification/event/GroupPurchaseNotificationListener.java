package com.hayeon.groupbuy.domain.notification.event;

import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;
import com.hayeon.groupbuy.domain.order.event.OrderPaidEvent;
import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.repository.OrderRepository;

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
    private final OrderRepository orderRepository;

    @EventListener
    public void handle(GroupPurchaseClosedEvent event) {

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

            if (event.isSuccess()) {

                notificationService.create(
                        userId,
                        NotificationStatus.GROUP_PURCHASE_SUCCESS,
                        "[" + title + "] 공동구매가 목표인원 달성을 성공하였습니다."

                );

            } else {

                notificationService.create(
                        userId,
                        NotificationStatus.GROUP_PURCHASE_FAILED,
                        "[" + title + "] 공동구매가 목표인원 달성을 실패했습니다."
                );
            }
        }

        log.info("공동구매 종료 알림 생성 완료 id={}", gpId);
        log.info("NotificationListener 종료");
    }

    @EventListener
    public void handle(OrderPaidEvent event) {

        Order order = orderRepository.findById(
                event.getOrderId()
        ).orElseThrow(
                () -> new IllegalArgumentException("ORDER_NOT_FOUND")
        );

        GroupPurchase gp =
                groupPurchaseRepository.findById(
                        order.getGroupPurchaseId()
                ).orElseThrow(
                        () -> new IllegalArgumentException("GROUP_PURCHASE_NOT_FOUND")
                );

        notificationService.createOrderPaid(
                order.getUserId(),
                gp.getTitle()
        );

        log.info(
                "결제 완료 알림 생성 orderId={}",
                order.getId()
        );
    }

    @EventListener
    public void handle(OrderCanceledEvent event) {

        Order order = orderRepository.findById(
                event.getOrderId()
        ).orElseThrow(
                () -> new IllegalArgumentException("ORDER_NOT_FOUND")
        );

        GroupPurchase gp =
                groupPurchaseRepository.findById(
                        order.getGroupPurchaseId()
                ).orElseThrow(
                        () -> new IllegalArgumentException("GROUP_PURCHASE_NOT_FOUND")
                );

        if (event.isAutoCanceled()) {

            notificationService.createOrderAutoCanceled(
                    order.getUserId(),
                    gp.getTitle()
            );

        } else {

            notificationService.createOrderCanceledByUser(
                    order.getUserId(),
                    gp.getTitle()
            );
        }

        log.info(
                "주문 취소 알림 생성 orderId={}",
                order.getId()
        );
    }
}