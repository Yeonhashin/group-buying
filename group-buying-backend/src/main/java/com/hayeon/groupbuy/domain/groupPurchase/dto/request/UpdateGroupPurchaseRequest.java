package com.hayeon.groupbuy.domain.groupPurchase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGroupPurchaseRequest {

    @Size(max = 100, message = "타이틀은 100자 이내로 입력해주세요.")
    private String title;

    @Size(max = 1000, message = "설명은 1000자 이내로 입력해주세요.")
    private String details;

    @Positive(message = "목표 금액은 1원 이상이어야 합니다.")
    private Integer targetPrice;

    @Min(value = 2, message = "목표 인원은 2명 이상이어야 합니다.")
    private Integer targetParticipants;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDt;
}