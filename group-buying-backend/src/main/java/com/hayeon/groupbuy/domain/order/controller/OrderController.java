package com.hayeon.groupbuy.domain.order.controller;

import com.hayeon.groupbuy.domain.order.dto.request.CreateOrderRequest;
import com.hayeon.groupbuy.domain.order.dto.response.OrderResponse;
import com.hayeon.groupbuy.domain.order.dto.response.MyOrderResponse;
import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.service.OrderService;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.hayeon.groupbuy.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my/orders")
    public List<MyOrderResponse> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return orderService.getMyOrders(user.getId());
    }

    // ===== 주문 생성 =====
    @PostMapping
    public OrderResponse createOrder(@AuthenticationPrincipal CustomUserDetails user,
                                     @RequestBody CreateOrderRequest request) {

        Long orderId = orderService.createOrder(
                user.getId(),
                request.getGroupPurchaseId()
        );

        return OrderResponse.from(orderId);
    }

    // ===== 단건 조회 =====
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId,
                                  @AuthenticationPrincipal CustomUserDetails user) {

        Order order = orderService.getOrder(orderId, user.getId());
        return OrderResponse.from(order);
    }

    // ===== 전체 조회 (status optional) =====
    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam Long userId,
                                         @RequestParam(required = false) OrderStatus status) {

        return orderService.getOrders(userId, status)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    // ===== 결제 처리 (Mock) =====
    @PostMapping("/{orderId}/pay")
    public void payOrder(@PathVariable Long orderId,
                         @RequestParam String paymentId) {

        orderService.payOrder(orderId, paymentId);
    }

    // ===== 주문 취소 =====
    @PostMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable Long orderId,
                            @AuthenticationPrincipal CustomUserDetails user) {

        orderService.cancelOrder(orderId, user.getId());
    }

}