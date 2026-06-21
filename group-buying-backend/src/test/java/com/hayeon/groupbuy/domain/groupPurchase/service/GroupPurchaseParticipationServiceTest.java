package com.hayeon.groupbuy.domain.groupPurchase.service;

import com.hayeon.groupbuy.domain.groupPurchase.compensation.entity.RedisFailLog;
import com.hayeon.groupbuy.domain.groupPurchase.compensation.repository.RedisFailLogRepository;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.JoinGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GroupPurchaseParticipationServiceTest {

    @InjectMocks
    private GroupPurchaseParticipationService participationService;

    @Mock private GroupPurchaseParticipationRepository participationRepository;
    @Mock private GroupPurchaseCountRedisRepository redisRepository;
    @Mock private GroupPurchaseRepository groupPurchaseRepository;
    @Mock private UserRepository userRepository;
    @Mock private RedisFailLogRepository redisFailLogRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getId()).willReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, null)
        );
    }

    private GroupPurchase mockRecruitingGroupPurchase() {
        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.RECRUITING);
        given(gp.getDeleteDt()).willReturn(null);
        given(gp.getStartDt()).willReturn(LocalDate.now().minusDays(1));
        given(gp.getEndDt()).willReturn(LocalDate.now().plusDays(7));
        given(gp.getTargetParticipants()).willReturn(10);
        return gp;
    }

    // ==================== join() ====================

    @Test
    @DisplayName("신규 참여 성공")
    void join_신규참여_성공() {
        setSecurityContext(1L);

        User user = mock(User.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        GroupPurchase gp = mockRecruitingGroupPurchase();
        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.empty());
        given(redisRepository.join(100L, 10)).willReturn(1L);

        participationService.join(100L, mock(JoinGroupPurchaseRequest.class));

        then(participationRepository).should().save(any(GroupPurchaseParticipation.class));
    }

    @Test
    @DisplayName("로그인 안 한 상태로 참여 시 UnauthorizedException")
    void join_로그인안함_예외() {
        assertThatThrownBy(() -> participationService.join(100L, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하지 않는 공동구매 참여 시 ResourceNotFoundException")
    void join_공동구매없음_예외() {
        setSecurityContext(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(groupPurchaseRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> participationService.join(999L, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("참여할 공동구매가 존재하지 않습니다");
    }

    @Test
    @DisplayName("RECRUITING 아닌 상태의 공동구매 참여 시 ResourceNotFoundException")
    void join_참여불가상태_예외() {
        setSecurityContext(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.COMPLETED);
        lenient().when(gp.getDeleteDt()).thenReturn(null);
        lenient().when(gp.getStartDt()).thenReturn(LocalDate.now().minusDays(1));
        lenient().when(gp.getEndDt()).thenReturn(LocalDate.now().plusDays(7));
        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        assertThatThrownBy(() -> participationService.join(100L, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("참여 불가능한 공동구매입니다");
    }

    @Test
    @DisplayName("이미 참여 중인 공동구매 재참여 시 ConflictException")
    void join_이미참여중_예외() {
        setSecurityContext(1L);
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));

        GroupPurchase gp = mock(GroupPurchase.class);
        lenient().when(gp.getStatus()).thenReturn(GroupPurchaseStatus.RECRUITING);
        lenient().when(gp.getDeleteDt()).thenReturn(null);
        lenient().when(gp.getStartDt()).thenReturn(LocalDate.now().minusDays(1));
        lenient().when(gp.getEndDt()).thenReturn(LocalDate.now().plusDays(7));
        lenient().when(gp.getTargetParticipants()).thenReturn(10);
        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        GroupPurchaseParticipation existing = mock(GroupPurchaseParticipation.class);
        given(existing.getStatus()).willReturn(ParticipationStatus.ACTIVE);
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.of(existing));

        assertThatThrownBy(() -> participationService.join(100L, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("이미 참여한 공동구매입니다");
    }

    @Test
    @DisplayName("Redis 인원 초과 시 ConflictException")
    void join_인원초과_예외() {
        setSecurityContext(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));

        GroupPurchase gp = mockRecruitingGroupPurchase();
        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.empty());
        given(redisRepository.join(100L, 10)).willReturn(-1L); // 인원 초과

        assertThatThrownBy(() -> participationService.join(100L, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("참여 인원이 초과되었습니다");
    }

    @Test
    @DisplayName("DB 저장 실패 시 Redis 롤백 및 RedisFailLog 저장")
    void join_DB저장실패_Redis롤백() {
        setSecurityContext(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));

        GroupPurchase gp = mockRecruitingGroupPurchase();
        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.empty());
        given(redisRepository.join(100L, 10)).willReturn(1L);
        given(participationRepository.save(any())).willThrow(new RuntimeException("DB 오류"));

        assertThatThrownBy(() -> participationService.join(100L, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("참여 처리 실패");

        then(redisRepository).should().cancel(100L);
        then(redisFailLogRepository).should().save(any(RedisFailLog.class));
    }

    @Test
    @DisplayName("취소 후 재참여 성공")
    void join_재참여_성공() {
        setSecurityContext(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));

        GroupPurchase gp = mockRecruitingGroupPurchase();
        given(groupPurchaseRepository.findById(100L)).willReturn(Optional.of(gp));

        GroupPurchaseParticipation existing = mock(GroupPurchaseParticipation.class);
        given(existing.getStatus()).willReturn(ParticipationStatus.CANCELED);
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.of(existing));
        given(redisRepository.join(100L, 10)).willReturn(1L);

        participationService.join(100L, mock(JoinGroupPurchaseRequest.class));

        then(existing).should().setStatus(ParticipationStatus.ACTIVE);
    }

    // ==================== cancel() ====================

    @Test
    @DisplayName("참여 취소 성공")
    void cancel_성공() {
        setSecurityContext(1L);

        GroupPurchaseParticipation participation = mock(GroupPurchaseParticipation.class);
        given(participation.getStatus()).willReturn(ParticipationStatus.ACTIVE);
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.of(participation));

        participationService.cancel(100L);

        then(participation).should().setStatus(ParticipationStatus.CANCELED);
        then(redisRepository).should().cancel(100L);
    }

    @Test
    @DisplayName("로그인 안 한 상태로 취소 시 UnauthorizedException")
    void cancel_로그인안함_예외() {
        assertThatThrownBy(() -> participationService.cancel(100L))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("참여 내역 없는 공동구매 취소 시 ConflictException")
    void cancel_참여내역없음_예외() {
        setSecurityContext(1L);
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> participationService.cancel(100L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("참여한 공동구매가 존재하지 않습니다");
    }

    @Test
    @DisplayName("이미 취소된 공동구매 재취소 시 ConflictException")
    void cancel_이미취소됨_예외() {
        setSecurityContext(1L);

        GroupPurchaseParticipation participation = mock(GroupPurchaseParticipation.class);
        given(participation.getStatus()).willReturn(ParticipationStatus.CANCELED);
        given(participationRepository.findByGroupPurchaseIdAndUserId(100L, 1L))
                .willReturn(Optional.of(participation));

        assertThatThrownBy(() -> participationService.cancel(100L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("이미 취소된 공동구매입니다");
    }
}