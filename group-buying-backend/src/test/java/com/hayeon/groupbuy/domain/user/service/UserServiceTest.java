package com.hayeon.groupbuy.domain.user.service;

import com.hayeon.groupbuy.domain.user.dto.request.SignUpRequest;
import com.hayeon.groupbuy.domain.user.dto.response.UserResponse;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.exception.UserNotFoundException;
import com.hayeon.groupbuy.global.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@test.com");
        signUpRequest.setPassword("password123!");
        signUpRequest.setNickname("테스트유저");
        SecurityContextHolder.clearContext(); // 각 테스트 전 SecurityContext 초기화
    }

    // ==================== signup() ====================

    @Test
    @DisplayName("정상 회원가입 성공 - save 호출 확인")
    void signup_성공() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(signUpRequest.getPassword())).willReturn("encodedPassword");

        // when
        userService.signup(signUpRequest);

        // then
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("이메일 중복 시 ConflictException 발생")
    void signup_이메일중복_예외() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(signUpRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("이미 존재하는 이메일");
    }

    @Test
    @DisplayName("비밀번호는 암호화되어 저장 - raw 비밀번호 저장 안 됨")
    void signup_비밀번호_암호화_확인() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode("password123!")).willReturn("encodedPassword");

        // when
        userService.signup(signUpRequest);

        // then - encode()가 호출됐는지 확인
        then(passwordEncoder).should().encode("password123!");
        // save()에 넘어간 User 객체의 비밀번호가 raw가 아닌지 확인
        then(userRepository).should().save(argThat(user ->
                !user.getPassword().equals("password123!")
        ));
    }

    @Test
    @DisplayName("회원가입 시 passwordEncoder.encode() 호출 검증")
    void signup_passwordEncoder_호출_검증() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        // when
        userService.signup(signUpRequest);

        // then
        then(passwordEncoder).should(times(1)).encode(signUpRequest.getPassword());
    }

    // ==================== checkEmail() ====================

    @Test
    @DisplayName("존재하는 이메일 → true 반환")
    void checkEmail_존재하는_이메일() {
        // given
        given(userRepository.existsByEmail("test@test.com")).willReturn(true);

        // when
        boolean result = userService.checkEmail("test@test.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일 → false 반환")
    void checkEmail_존재하지않는_이메일() {
        // given
        given(userRepository.existsByEmail("notexist@test.com")).willReturn(false);

        // when
        boolean result = userService.checkEmail("notexist@test.com");

        // then
        assertThat(result).isFalse();
    }

    // ==================== me() ====================

    @Test
    @DisplayName("정상 조회 성공 - UserResponse 반환")
    void me_정상조회() {
        // given
        setSecurityContext(1L); // SecurityContext에 userId 1L 심기

        User mockUser = mock(User.class);
        given(mockUser.getEmail()).willReturn("test@test.com");
        given(mockUser.getNickname()).willReturn("테스트유저");
        given(mockUser.getCreatedAt()).willReturn(null);
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));

        // when
        UserResponse response = userService.me();

        // then
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.nickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("SecurityContext에 인증 정보 없으면 UnauthorizedException")
    void me_인증정보없음_예외() {
        // given - SecurityContext 비어있음 (setUp에서 clearContext 했으니까)

        // when & then
        assertThatThrownBy(() -> userService.me())
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("userId는 있는데 DB에 없으면 UserNotFoundException")
    void me_사용자없음_예외() {
        // given
        setSecurityContext(999L); // 존재하지 않는 userId
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.me())
                .isInstanceOf(UserNotFoundException.class);
    }

    // ==================== 헬퍼 메서드 ====================

    private void setSecurityContext(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getId()).willReturn(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}