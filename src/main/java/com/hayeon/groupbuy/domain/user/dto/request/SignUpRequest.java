package com.hayeon.groupbuy.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    @Email
    private String email;

    @NotBlank
    private String password;

    private String nickname;
}