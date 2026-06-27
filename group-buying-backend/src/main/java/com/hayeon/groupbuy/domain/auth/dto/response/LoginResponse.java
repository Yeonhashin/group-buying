package com.hayeon.groupbuy.domain.auth.dto.response;

import com.hayeon.groupbuy.domain.user.enums.UserRole;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long userId;
    private String nickname;
    private UserRole role;
}