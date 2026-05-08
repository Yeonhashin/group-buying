package com.hayeon.groupbuy.domain.order.repository;

import com.hayeon.groupbuy.domain.order.entity.Order;
import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByGroupPurchaseId(Long groupPurchaseId);

    // 중복 주문 방지
    boolean existsByUserIdAndGroupPurchaseId(Long userId, Long groupPurchaseId);

    // 사용자 주문 전체 조회
    List<Order> findByUserId(Long userId);

    // 사용자 + 상태 필터 조회
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // 공동구매 기준 조회 (스케줄러/관리용)
    List<Order> findByGroupPurchaseId(Long groupPurchaseId);

    // 단건 조회 (보안/소유 검증용)
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // 미결제 주문 조회
    List<Order> findByStatusAndPaymentStatusInAndCreateDtBefore(
      OrderStatus status,
      List<PaymentStatus> paymentStatuses,
      LocalDateTime createDt
    );
}