package com.hayeon.groupbuy.domain.auth.service;

import com.hayeon.groupbuy.domain.auth.dto.request.LoginRequest;
import com.hayeon.groupbuy.domain.auth.dto.response.LoginResponse;
import com.hayeon.groupbuy.domain.user.dto.request.SignUpRequest;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.domain.user.service.UserService;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.hayeon.groupbuy.config.TestRedisConfig;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
class AuthIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@test.com");
        signUpRequest.setPassword("password123!");
        signUpRequest.setNickname("테스트유저");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123!");
    }

    @Test
    @DisplayName("회원가입 후 DB 실제 저장 확인")
    void 회원가입_후_DB_저장_확인() {
        userService.signup(signUpRequest);

        boolean exists = userRepository.existsByEmail("test@test.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("회원가입 후 비밀번호 암호화 저장 확인")
    void 회원가입_비밀번호_암호화_확인() {
        userService.signup(signUpRequest);

        User user = userRepository.findByEmail("test@test.com").orElseThrow();
        assertThat(user.getPassword()).isNotEqualTo("password123!");
        assertThat(user.getPassword()).startsWith("$2a$");
    }

    @Test
    @DisplayName("로그인 성공 시 토큰 반환 확인")
    void 로그인_성공_토큰_반환() {
        userService.signup(signUpRequest);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("중복 이메일 회원가입 시 예외 발생")
    void 중복_이메일_회원가입_예외() {
        userService.signup(signUpRequest);

        assertThatThrownBy(() -> userService.signup(signUpRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외 발생")
    void 잘못된_비밀번호_로그인_예외() {
        userService.signup(signUpRequest);
        loginRequest.setPassword("wrongPassword!");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class);
    }
}