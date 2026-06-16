package com.hayeon.groupbuy.domain.auth.service;

import com.hayeon.groupbuy.domain.auth.dto.request.LoginRequest;
import com.hayeon.groupbuy.domain.auth.dto.response.LoginResponse;
import com.hayeon.groupbuy.domain.auth.jwt.JwtProvider;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123!");
    }

    @Test
    @DisplayName("로그인 성공 - LoginResponse 반환 확인")
    void login_성공() {
        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(1L);
        given(mockUser.getNickname()).willReturn("테스트유저");
        given(mockUser.getPassword()).willReturn("encodedPassword");
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("password123!", "encodedPassword")).willReturn(true);
        given(jwtProvider.createToken(1L)).willReturn("jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("로그인 성공 시 jwtProvider.createToken() 호출 확인")
    void login_성공시_토큰생성_호출확인() {
        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(1L);
        given(mockUser.getNickname()).willReturn("테스트유저");
        given(mockUser.getPassword()).willReturn("encodedPassword");
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("password123!", "encodedPassword")).willReturn(true);
        given(jwtProvider.createToken(1L)).willReturn("jwt-token");

        authService.login(loginRequest);

        then(jwtProvider).should(times(1)).createToken(1L);
    }

    @Test
    @DisplayName("존재하지 않는 이메일 → UnauthorizedException")
    void login_이메일없음_예외() {
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("비밀번호 불일치 → UnauthorizedException")
    void login_비밀번호불일치_예외() {
        User mockUser = mock(User.class);
        given(mockUser.getPassword()).willReturn("encodedPassword");
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("password123!", "encodedPassword")).willReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class);
    }
}