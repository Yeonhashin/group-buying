package com.hayeon.groupbuy.domain.groupPurchase.repository;

import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.order.dto.response.MyOrderResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface GroupPurchaseParticipationRepository
        extends JpaRepository<GroupPurchaseParticipation, Long> {

    Optional<GroupPurchaseParticipation> findByGroupPurchaseIdAndUserId(
            Long groupPurchaseId,
            Long userId
    );

    boolean existsByGroupPurchaseIdAndUserIdAndStatus(
            Long groupPurchaseId,
            Long userId,
            ParticipationStatus status
    );

    long countByGroupPurchaseIdAndStatus(
            Long groupPurchaseId,
            ParticipationStatus status
    );

    List<GroupPurchaseParticipation> findByGroupPurchaseIdAndStatus(
            Long groupPurchaseId,
            ParticipationStatus status
    );

    @Query("""
        SELECT new com.hayeon.groupbuy.domain.order.dto.response.MyOrderResponse(
            gp.id,
            gp.title,
            gp.status,
            o.id,
            o.status,
            o.totalPrice
        )
        FROM GroupPurchaseParticipation p
        JOIN p.groupPurchase gp
        LEFT JOIN Order o 
            ON o.groupPurchaseId = gp.id AND o.userId = p.user.id
        WHERE p.user.id = :userId
        ORDER BY gp.id DESC
    """)
    List<MyOrderResponse> findMyOrders(@Param("userId") Long userId);
}