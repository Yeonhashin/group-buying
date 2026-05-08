package com.hayeon.groupbuy.domain.groupPurchase.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinGroupPurchaseRequest {

    @Min(1)
    @Max(1)
    private Integer quantity;
}