package com.hayeon.groupbuy.domain.order.service;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;
import com.hayeon.groupbuy.domain.order.event.OrderCanceledEvent;
import com.hayeon.groupbuy.domain.order.event.OrderPaidEvent;
import com.hayeon.groupbuy.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock private OrderRepository orderRepository;
    @Mock private GroupPurchaseParticipationRepository participationRepository;
    @Mock private GroupPurchaseRepository groupPurchaseRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    // ==================== createOrder() ====================

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_성공() {
        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getTargetPrice()).willReturn(10000);
        given(groupPurchaseRepository.findById(1L)).willReturn(Optional.of(gp));
        given(orderRepository.existsByUserIdAndGroupPurchaseId(100L, 1L)).willReturn(false);
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(100L, 1L);

        then(orderRepository).should().save(any(Order.class));
    }

    @Test
    @DisplayName("존재하지 않는 공동구매로 주문 생성 시 예외")
    void createOrder_공동구매없음_예외() {
        given(groupPurchaseRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(100L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GROUP_PURCHASE_NOT_FOUND");
    }

    @Test
    @DisplayName("이미 주문한 공동구매 재주문 시 예외")
    void createOrder_중복주문_예외() {
        GroupPurchase gp = mock(GroupPurchase.class);
        lenient().when(gp.getTargetPrice()).thenReturn(10000); // lenient 처리
        given(groupPurchaseRepository.findById(1L)).willReturn(Optional.of(gp));
        given(orderRepository.existsByUserIdAndGroupPurchaseId(100L, 1L)).willReturn(true);

        assertThatThrownBy(() -> orderService.createOrder(100L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ALREADY_ORDERED");
    }

    // ==================== getOrder() ====================

    @Test
    @DisplayName("주문 단건 조회 성공")
    void getOrder_성공() {
        Order order = mock(Order.class);
        given(orderRepository.findByIdAndUserId(1L, 100L)).willReturn(Optional.of(order));

        Order result = orderService.getOrder(1L, 100L);

        assertThat(result).isEqualTo(order);
    }

    @Test
    @DisplayName("존재하지 않는 주문 조회 시 예외")
    void getOrder_없음_예외() {
        given(orderRepository.findByIdAndUserId(999L, 100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(999L, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER_NOT_FOUND");
    }

    // ==================== getOrders() ====================

    @Test
    @DisplayName("상태 없이 전체 주문 조회")
    void getOrders_전체조회() {
        given(orderRepository.findByUserId(100L)).willReturn(List.of(mock(Order.class)));

        List<Order> result = orderService.getOrders(100L, null);

        assertThat(result).hasSize(1);
        then(orderRepository).should(never()).findByUserIdAndStatus(any(), any());
    }

    @Test
    @DisplayName("상태별 주문 조회")
    void getOrders_상태별조회() {
        given(orderRepository.findByUserIdAndStatus(100L, OrderStatus.CREATED))
                .willReturn(List.of(mock(Order.class)));

        List<Order> result = orderService.getOrders(100L, OrderStatus.CREATED);

        assertThat(result).hasSize(1);
        then(orderRepository).should(never()).findByUserId(any());
    }

    // ==================== payOrder() ====================

    @Test
    @DisplayName("결제 성공 - OrderPaidEvent 발행 확인")
    void payOrder_성공_이벤트발행() {
        Order order = mock(Order.class);
        given(order.getStatus()).willReturn(OrderStatus.CREATED);
        given(order.getPaymentStatus()).willReturn(PaymentStatus.READY);
        given(order.getId()).willReturn(1L);
        given(order.getUserId()).willReturn(100L);
        given(order.getGroupPurchaseId()).willReturn(10L);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getTitle()).willReturn("테스트 공동구매");
        given(groupPurchaseRepository.findById(10L)).willReturn(Optional.of(gp));

        orderService.payOrder(1L, "payment-123");

        then(order).should().markPaid("payment-123");

        ArgumentCaptor<OrderPaidEvent> captor = ArgumentCaptor.forClass(OrderPaidEvent.class);
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("존재하지 않는 주문 결제 시 예외")
    void payOrder_주문없음_예외() {
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.payOrder(999L, "payment-123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER_NOT_FOUND");
    }

    @Test
    @DisplayName("CREATED 아닌 주문 결제 시 예외")
    void payOrder_잘못된주문상태_예외() {
        Order order = mock(Order.class);
        given(order.getStatus()).willReturn(OrderStatus.CANCELED);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.payOrder(1L, "payment-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INVALID_ORDER_STATUS");
    }

    @Test
    @DisplayName("READY/FAILED 아닌 결제 상태에서 결제 시 예외")
    void payOrder_잘못된결제상태_예외() {
        Order order = mock(Order.class);
        given(order.getStatus()).willReturn(OrderStatus.CREATED);
        given(order.getPaymentStatus()).willReturn(PaymentStatus.PAID);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.payOrder(1L, "payment-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INVALID_PAYMENT_STATUS");
    }

    // ==================== cancelOrder() ====================

    @Test
    @DisplayName("결제 취소 성공 - OrderCanceledEvent 발행 확인")
    void cancelOrder_성공_이벤트발행() {
        Order order = mock(Order.class);
        given(order.getPaymentStatus()).willReturn(PaymentStatus.PAID);
        given(order.getCreateDt()).willReturn(LocalDateTime.now().minusHours(1));
        given(order.getId()).willReturn(1L);
        given(order.getUserId()).willReturn(100L);
        given(order.getGroupPurchaseId()).willReturn(10L);
        given(orderRepository.findByIdAndUserId(1L, 100L)).willReturn(Optional.of(order));

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getTitle()).willReturn("테스트 공동구매");
        given(groupPurchaseRepository.findById(10L)).willReturn(Optional.of(gp));

        orderService.cancelOrder(1L, 100L);

        then(order).should().cancelPayment();

        ArgumentCaptor<OrderCanceledEvent> captor = ArgumentCaptor.forClass(OrderCanceledEvent.class);
        then(eventPublisher).should().publishEvent(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("존재하지 않는 주문 취소 시 예외")
    void cancelOrder_주문없음_예외() {
        given(orderRepository.findByIdAndUserId(999L, 100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(999L, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER_NOT_FOUND");
    }

    @Test
    @DisplayName("미결제 주문 취소 시 예외")
    void cancelOrder_미결제주문_예외() {
        Order order = mock(Order.class);
        given(order.getPaymentStatus()).willReturn(PaymentStatus.READY);
        given(orderRepository.findByIdAndUserId(1L, 100L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("NOT_PAID_ORDER");
    }

    @Test
    @DisplayName("48시간 초과 주문 취소 시 예외")
    void cancelOrder_48시간초과_예외() {
        Order order = mock(Order.class);
        given(order.getPaymentStatus()).willReturn(PaymentStatus.PAID);
        given(order.getCreateDt()).willReturn(LocalDateTime.now().minusHours(49));
        given(orderRepository.findByIdAndUserId(1L, 100L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CANCEL_PERIOD_EXPIRED");
    }
}