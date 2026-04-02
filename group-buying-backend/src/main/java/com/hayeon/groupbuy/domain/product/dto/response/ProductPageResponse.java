package com.hayeon.groupbuy.domain.product.dto.response;

import com.hayeon.groupbuy.domain.product.dto.response.ProductResponse;
import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> content,
        int page,
        int size,
        int totalPages,
        long totalElements
) {}