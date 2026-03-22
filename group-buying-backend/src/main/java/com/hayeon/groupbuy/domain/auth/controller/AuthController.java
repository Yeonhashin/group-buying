package com.hayeon.groupbuy.domain.auth.controller;

import com.hayeon.groupbuy.domain.auth.dto.request.LoginRequest;
import com.hayeon.groupbuy.domain.auth.dto.response.LoginResponse;
import com.hayeon.groupbuy.domain.auth.service.AuthService;
import com.hayeon.groupbuy.global.response.CommonResponse;

import java.time.LocalDateTime;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

//    @PostMapping("/login")
//    public LoginResponse login (@Valid @RequestBody LoginRequest request) {
//        return authService.login(request);
//    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        return ResponseEntity.ok(
                CommonResponse.<String>builder()
                        .success(true)
                        .message("로그인 성공")
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .data(token)
                        .build()
        );
    }
}