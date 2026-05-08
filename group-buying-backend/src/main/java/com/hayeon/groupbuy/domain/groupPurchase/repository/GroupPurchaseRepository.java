package com.hayeon.groupbuy.domain.groupPurchase.repository;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {

    /**
     * 리스트 조회
     */
    @EntityGraph(attributePaths = {"product", "user"})
    Page<GroupPurchase> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"product", "user"})
    Page<GroupPurchase> findByTitleContainingOrDetailsContaining(
            String keyword1,
            String keyword2,
            Pageable pageable
    );

    /**
     * 상태 조회
     */
    List<GroupPurchase> findAllByStatus(GroupPurchaseStatus status);

    List<GroupPurchase> findAllByStatusAndDeleteDtIsNull(GroupPurchaseStatus status);

    /**
     * 기본 PK 조회 (LAZY 유지)
     */
    Optional<GroupPurchase> findById(Long id);

    /**
     * 🔥 상세 조회 전용 (핵심)
     */
    @EntityGraph(attributePaths = {"product", "user"})
    @Query("SELECT gp FROM GroupPurchase gp WHERE gp.id = :id")
    Optional<GroupPurchase> findDetailById(@Param("id") Long id);
}