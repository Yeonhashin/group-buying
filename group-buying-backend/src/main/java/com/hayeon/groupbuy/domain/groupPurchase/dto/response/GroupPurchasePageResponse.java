package com.hayeon.groupbuy.domain.groupPurchase.dto.response;

import com.hayeon.groupbuy.domain.groupPurchase.dto.response.GroupPurchaseResponse;
import java.util.List;

public record GroupPurchasePageResponse(
        List<GroupPurchaseResponse> content,
        int page,
        int size,
        int totalPages,
        long totalElements
) {}