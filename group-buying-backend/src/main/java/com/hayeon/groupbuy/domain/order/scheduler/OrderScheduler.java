package com.hayeon.groupbuy.domain.order.scheduler;

import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;
import com.hayeon.groupbuy.domain.order.repository.OrderRepository;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    // 5분마다 실행
    @Scheduled(fixedDelay = 300000)
    public void cancelUnpaidOrders() {

        LocalDateTime 기준시간 = LocalDateTime.now().minusHours(24);

        List<Order> orders =
                orderRepository.findByStatusAndPaymentStatusInAndCreateDtBefore(
                        OrderStatus.CREATED,
                        List.of(PaymentStatus.READY, PaymentStatus.FAILED),
                        기준시간
                );

        log.info("미결제 주문 {}건 발견", orders.size());

        for (Order order : orders) {
            try {
                cancelSingleOrder(order);
            } catch (Exception e) {
                log.error("주문 취소 실패 orderId={}", order.getId(), e);
            }
        }
    }

    // ===== 건별 트랜잭션 =====
    @Transactional
    public void cancelSingleOrder(Order order) {

        // 상태 검증 (안전장치)
        if (order.getStatus() != OrderStatus.CREATED) {
            return;
        }

        order.cancel();

        eventPublisher.publishEvent(
                new OrderCanceledEvent(
                        order.getId(),
                        order.getUserId()
                )
        );

        log.info("미결제 주문 자동 취소 완료 id={}", order.getId());
    }
}