package com.hayeon.groupbuy.domain.order.scheduler;

import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;
import com.hayeon.groupbuy.domain.order.repository.OrderRepository;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;

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
    private final GroupPurchaseRepository groupPurchaseRepository;

    // 5분마다 실행
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void cancelUnpaidOrders() {

        LocalDateTime 기준시간 = LocalDateTime.now().minusHours(48);

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

    // 단일 주문 처리 (같은 트랜잭션 안에서 동작)
    private void cancelSingleOrder(Order order) {

        if (order.getStatus() != OrderStatus.CREATED) {
            return;
        }

        log.info("취소 전 status={}", order.getStatus());

        // 1. 상태 변경 (Dirty Checking 대상)
        order.cancel();

        log.info("취소 후 status={}", order.getStatus());

        // 2. 그룹 구매 정보 조회 (알림 메시지용)
        GroupPurchase gp =
                groupPurchaseRepository.findById(order.getGroupPurchaseId())
                        .orElseThrow(() -> new IllegalArgumentException("GROUP_PURCHASE_NOT_FOUND"));

        // 3. 이벤트 발행 (자동취소 플래그 true)
        eventPublisher.publishEvent(
                new OrderCanceledEvent(
                        order.getId(),
                        order.getUserId(),
                        gp.getTitle(),
                        true
                )
        );

        log.info("미결제 주문 자동 취소 완료 id={}", order.getId());
    }
}