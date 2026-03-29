package com.hayeon.groupbuy.domain.product.dto.response;

import com.hayeon.groupbuy.domain.product.entity.Product;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        Long userId,
        String name,
        String details,
        String imageUrl,
        LocalDateTime createDt
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getUserId(),
                product.getName(),
                product.getDetails(),
                product.getImageUrl(),
                product.getCreateDt()
        );
    }
}