package com.hayeon.groupbuy.domain.user.service;

import com.hayeon.groupbuy.domain.user.dto.request.SignUpRequest;
import com.hayeon.groupbuy.domain.user.dto.response.UserResponse;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.UserNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignUpRequest request) {
        // 1. 이메일 중복 체크
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 엔티티 생성
        User user = User.create(
                request.getEmail(),
                encodedPassword,
                request.getNickname()
        );

        // 4. 저장
        userRepository.save(user);
    }

    public UserResponse me() {
        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("로그인 필요"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        return new UserResponse(user.getEmail(), user.getNickname(), user.getCreatedAt());
    }
}