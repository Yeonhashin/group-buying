package com.hayeon.groupbuy.domain.groupPurchase.compensation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "redis_fail_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisFailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupPurchaseId;
    private Long userId;

    @Column(nullable = false)
    private String action; // INCR or DECR

    private LocalDateTime createDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }
}