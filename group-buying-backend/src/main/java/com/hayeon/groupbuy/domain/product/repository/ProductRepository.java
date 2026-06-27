package com.hayeon.groupbuy.domain.product.repository;

import com.hayeon.groupbuy.domain.product.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingOrDetailsContaining(
            String keyword1,
            String keyword2,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.deleteDt IS NULL")
    Page<Product> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.deleteDt IS NULL AND (p.name LIKE %:keyword% OR p.details LIKE %:keyword%)")
    Page<Product> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}