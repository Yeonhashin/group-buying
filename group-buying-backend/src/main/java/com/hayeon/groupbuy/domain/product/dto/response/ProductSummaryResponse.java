package com.hayeon.groupbuy.domain.product.dto.response;

import com.hayeon.groupbuy.domain.product.entity.Product;

import java.time.LocalDateTime;

public record ProductSummaryResponse(
        Long id,
        String name,
        String imageUrl,
        Integer price,
        Integer stock
) {
    public static ProductSummaryResponse from(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock()
        );
    }
}