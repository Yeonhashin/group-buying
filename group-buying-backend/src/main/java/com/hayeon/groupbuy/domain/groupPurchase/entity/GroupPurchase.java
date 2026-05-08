package com.hayeon.groupbuy.domain.groupPurchase.entity;

import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.product.entity.Product;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.UpdateGroupPurchaseRequest;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "group_purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "target_price", nullable = false)
    private Integer targetPrice;

    @Column(name = "target_participants", nullable = false)
    private Integer targetParticipants;

    @Column(name = "start_dt")
    private LocalDate startDt;

    @Column(name = "end_dt")
    private LocalDate endDt;

    @Enumerated(EnumType.STRING)
    private GroupPurchaseStatus status;

    @Column(name = "create_dt", updatable = false)
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDt = LocalDateTime.now();
    }

    public static GroupPurchase create(User user, Product product, String title, String details,
                                       Integer targetPrice, Integer targetParticipants,
                                       LocalDate startDt, LocalDate endDt) {

        GroupPurchase groupPurchase = new GroupPurchase();
        groupPurchase.user = user;
        groupPurchase.product = product;
        groupPurchase.title = title;
        groupPurchase.details = details;
        groupPurchase.targetPrice = targetPrice;
        groupPurchase.targetParticipants = targetParticipants;
        groupPurchase.startDt = startDt;
        groupPurchase.endDt = endDt;
        groupPurchase.status = GroupPurchaseStatus.RECRUITING;

        return groupPurchase;
    }

    public void updateFromDto(UpdateGroupPurchaseRequest dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getDetails() != null) this.details = dto.getDetails();
        if (dto.getTargetPrice() != null) this.targetPrice = dto.getTargetPrice();
        if (dto.getTargetParticipants() != null) this.targetParticipants = dto.getTargetParticipants();
        if (dto.getStartDt() != null) this.startDt = dto.getStartDt();
        if (dto.getEndDt() != null) this.endDt = dto.getEndDt();
    }

    public void updateStatus(GroupPurchaseStatus status) {
        this.status = status;
    }
}