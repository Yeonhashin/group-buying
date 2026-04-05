package com.hayeon.groupbuy.domain.groupPurchase.repository;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    Page<GroupPurchase> findByTitleContainingOrDetailsContaining (
            String keyword1,
            String keyword2,
            Pageable pageable
    );
}