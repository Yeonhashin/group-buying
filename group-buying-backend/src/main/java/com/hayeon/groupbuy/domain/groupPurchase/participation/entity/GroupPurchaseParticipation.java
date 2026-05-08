package com.hayeon.groupbuy.domain.groupPurchase.participation.entity;

import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(
        name = "group_purchases_participation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_group_user",
                        columnNames = {"group_purchase_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPurchaseParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_purchase_id", nullable = false)
    private GroupPurchase groupPurchase;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @Column(name = "create_dt", updatable = false)
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDt = LocalDateTime.now();
    }

}