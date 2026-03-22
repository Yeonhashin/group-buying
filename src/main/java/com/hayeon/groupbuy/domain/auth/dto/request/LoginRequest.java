package com.hayeon.groupbuy.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @Email
    private String email;

    @NotBlank
    private String password;
}