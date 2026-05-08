package com.hayeon.groupbuy.domain.order.entity;

import com.hayeon.groupbuy.domain.order.enums.OrderStatus;
import com.hayeon.groupbuy.domain.order.enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="group_purchase_id", nullable = false)
    private Long groupPurchaseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name="payment_id")
    private String paymentId;

    @Column(name="total_price", nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name="ordered_dt")
    private LocalDateTime orderedDt;

    @Column(name="create_dt")
    private LocalDateTime createDt;

    @Column(name="update_dt")
    private LocalDateTime updateDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();

        if (this.orderedDt == null) {
            this.orderedDt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDt = LocalDateTime.now();
    }

    public static Order create(Long userId,
                               Long groupPurchaseId,
                               Integer totalPrice) {

        Order order = new Order();
        order.userId = userId;
        order.groupPurchaseId = groupPurchaseId;
        order.totalPrice = totalPrice;
        order.status = OrderStatus.CREATED;
        order.paymentStatus = PaymentStatus.READY; // 추가
        order.orderedDt = LocalDateTime.now();
        order.createDt = LocalDateTime.now();
        order.updateDt = LocalDateTime.now();

        return order;
    }

    // 결제 성공
    public void markPaid(String paymentId) {
        this.paymentStatus = PaymentStatus.PAID;
        this.paymentId = paymentId;
    }

    // 결제 실패
    public void markFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    // 환불
    public void refund() {
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    // 취소 (스케쥴용 자동취소)
    public void cancel() {
        this.status = OrderStatus.CANCELED;
        this.paymentStatus = PaymentStatus.FAILED;
    }
}