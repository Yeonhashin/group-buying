package com.hayeon.groupbuy.domain.order.service;

import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.repository.OrderRepository;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;

import org.springframework.context.ApplicationEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.hayeon.groupbuy.domain.order.dto.response.MyOrderResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final GroupPurchaseParticipationRepository participationRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;

    private final ApplicationEventPublisher eventPublisher;

    public List<MyOrderResponse> getMyOrders(Long userId) {
        return participationRepository.findMyOrders(userId);
    }

    // ===== 주문 생성 (테스트용) =====
    @Transactional
    public Long createOrder(Long userId, Long gpId) {

        GroupPurchase gp = groupPurchaseRepository.findById(gpId)
                .orElseThrow(() -> new IllegalArgumentException("GROUP_PURCHASE_NOT_FOUND"));

        if (orderRepository.existsByUserIdAndGroupPurchaseId(userId, gpId)) {
            throw new IllegalStateException("ALREADY_ORDERED");
        }

        Order order = Order.create(
                userId,
                gpId,
                gp.getTargetPrice()
        );

        orderRepository.save(order);

        return order.getId();
    }

    // ===== 단건 조회 =====
    public Order getOrder(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));
    }

    // ===== 사용자 주문 조회 =====
    public List<Order> getOrders(Long userId, OrderStatus status) {

        if (status == null) {
            return orderRepository.findByUserId(userId);
        }

        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    // ===== 결제 =====
    @Transactional
    public void payOrder(Long orderId, String paymentId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

        // 주문 상태 체크
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("INVALID_ORDER_STATUS");
        }

        // 결제 상태 체크
        if (order.getPaymentStatus() != PaymentStatus.READY &&
                order.getPaymentStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("INVALID_PAYMENT_STATUS");
        }

        order.markPaid(paymentId);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

        order.cancel();

        eventPublisher.publishEvent(
                new OrderCanceledEvent(order.getId(), order.getUserId())
        );
    }
}