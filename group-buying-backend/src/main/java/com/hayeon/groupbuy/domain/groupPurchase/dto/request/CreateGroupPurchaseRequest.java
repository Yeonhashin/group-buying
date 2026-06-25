package com.hayeon.groupbuy.domain.groupPurchase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupPurchaseRequest {

    @NotNull(message = "공동 구매를 생성할 상품을 선택해주세요.")
    private Long productId;

    @NotBlank(message = "공동 구매 타이틀을 입력해주세요.")
    @Size(max = 100, message = "타이틀은 100자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "생성할 공동구매의 설명을 입력해주세요.")
    @Size(max = 1000, message = "설명은 1000자 이내로 입력해주세요.")
    private String details;

    @NotNull(message = "목표 금액을 입력해주세요.")
    @Positive(message = "목표 금액은 1원 이상이어야 합니다.")
    private Integer targetPrice;

    @NotNull(message = "목표 참여 인원을 입력해주세요.")
    @Min(value = 2, message = "목표 인원은 2명 이상이어야 합니다.")
    private Integer targetParticipants;

    @NotNull(message = "시작 시간을 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDt;

    @NotNull(message = "종료 시간을 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDt;
}