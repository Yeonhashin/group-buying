package com.hayeon.groupbuy.domain.groupPurchase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.time.LocalDateTime;

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

    private Integer targetPrice;
    private Integer targetParticipants;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDt;
}