package com.hayeon.groupbuy.domain.order.event.listener;

import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCanceledEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCanceledEvent event) {

        log.info("주문 취소 이벤트 수신 orderId={}", event.getOrderId());

        notificationService.createOrderCanceled(
                event.getUserId(),
                event.getOrderId()
        );
    }
}