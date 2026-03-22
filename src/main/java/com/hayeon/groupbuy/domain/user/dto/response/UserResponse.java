package com.hayeon.groupbuy.domain.user.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        String email,
        String nickname,
        LocalDateTime createdAt
) {}