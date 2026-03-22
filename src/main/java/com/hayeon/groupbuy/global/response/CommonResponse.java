package com.hayeon.groupbuy.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private int status;
    private int code;
    private LocalDateTime timestamp;

    // 성공
    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .data(data)
                .code(200)
                .status(200)
                .message("OK")
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 실패
    public static <T> CommonResponse<T> fail(String message, int code) {
        return CommonResponse.<T>builder()
                .success(false)
                .data(null)
                .message(message)
                .code(code)
                .status(code)
                .timestamp(LocalDateTime.now())
                .build();
    }
}