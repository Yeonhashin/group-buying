package com.hayeon.groupbuy.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "상품명을 입력해주세요.")
    @Size(max = 100, message = "상품명은 100자 이내로 입력해주세요.")
    private String name;

    @NotBlank(message = "상품설명을 입력해주세요.")
    @Size(max = 1000, message = "상품설명은 1000자 이내로 입력해주세요.")
    private String details;

    @Pattern(
            regexp = "^(https?|ftp)://.*$",
            message = "올바른 이미지 URL 형식이 아닙니다."
    )
    private String imageUrl;
}