package com.hayeon.groupbuy.domain.auth.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long userId;
    private String nickname;
}