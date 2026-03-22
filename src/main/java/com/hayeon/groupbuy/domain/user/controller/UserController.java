package com.hayeon.groupbuy.domain.user.controller;

import com.hayeon.groupbuy.domain.user.dto.request.SignUpRequest;
import com.hayeon.groupbuy.domain.user.dto.response.UserResponse;
import com.hayeon.groupbuy.domain.user.service.UserService;
import com.hayeon.groupbuy.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Void>> signup(@RequestBody SignUpRequest request) {

        userService.signup(request);

        return ResponseEntity.ok(
                CommonResponse.success(null)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserResponse>> getMyInfo() {

        UserResponse response = userService.me();

        return ResponseEntity.ok(
                CommonResponse.success(response)
        );
    }
}