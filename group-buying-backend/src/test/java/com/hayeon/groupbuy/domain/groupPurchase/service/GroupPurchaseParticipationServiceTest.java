package com.hayeon.groupbuy.domain.groupPurchase.service;

import com.hayeon.groupbuy.domain.groupPurchase.compensation.repository.RedisFailLogRepository;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.JoinGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock private NotificationService notificationService;

    private static final Long USER_ID = 1L;
    private static final Long GP_ID = 100L;
    private static final String GP_TITLE = "테스트 공동구매";

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

    /** 상태 체크를 통과하는 최소 stub (모집 중, 삭제 안 됨, 기간 내) */
    private GroupPurchase recruitingGroupPurchase() {
        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getDeleteDt()).willReturn(null);
        given(gp.getStatus()).willReturn(GroupPurchaseStatus.RECRUITING);
        given(gp.getStartDt()).willReturn(LocalDate.now().minusDays(1));
        given(gp.getEndDt()).willReturn(LocalDate.now().plusDays(7));
        return gp;
    }

    // ==================== join() ====================

    @Test
    @DisplayName("공동구매 참여 성공 시 PARTICIPATION_JOINED 알림이 생성된다")
    void join_성공시_참여완료_알림_생성() {
        setSecurityContext(USER_ID);
        GroupPurchase gp = recruitingGroupPurchase();
        given(gp.getTitle()).willReturn(GP_TITLE);
        given(gp.getTargetParticipants()).willReturn(10);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(mock(User.class)));
        given(groupPurchaseRepository.findById(GP_ID)).willReturn(Optional.of(gp));
        given(participationRepository.findByGroupPurchaseIdAndUserId(GP_ID, USER_ID)).willReturn(Optional.empty());
        given(redisRepository.join(eq(GP_ID), eq(10))).willReturn(1L);

        participationService.join(GP_ID, mock(JoinGroupPurchaseRequest.class));

        then(notificationService).should().createParticipationJoined(USER_ID, GP_TITLE);
    }

    @Test
    @DisplayName("공동구매 참여 실패(인원 초과) 시 알림이 생성되지 않는다")
    void join_인원초과_알림_미생성() {
        setSecurityContext(USER_ID);
        GroupPurchase gp = recruitingGroupPurchase();
        given(gp.getTargetParticipants()).willReturn(10);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(mock(User.class)));
        given(groupPurchaseRepository.findById(GP_ID)).willReturn(Optional.of(gp));
        given(participationRepository.findByGroupPurchaseIdAndUserId(GP_ID, USER_ID)).willReturn(Optional.empty());
        given(redisRepository.join(eq(GP_ID), eq(10))).willReturn(-1L);

        assertThatThrownBy(() -> participationService.join(GP_ID, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ConflictException.class);

        then(notificationService).should(never()).createParticipationJoined(any(), any());
    }

    @Test
    @DisplayName("이미 참여 중인 공동구매 재참여 시 알림이 생성되지 않는다")
    void join_이미참여중_알림_미생성() {
        setSecurityContext(USER_ID);
        GroupPurchase gp = recruitingGroupPurchase();

        GroupPurchaseParticipation existing = mock(GroupPurchaseParticipation.class);
        given(existing.getStatus()).willReturn(ParticipationStatus.ACTIVE);

        given(userRepository.findById(USER_ID)).willReturn(Optional.of(mock(User.class)));
        given(groupPurchaseRepository.findById(GP_ID)).willReturn(Optional.of(gp));
        given(participationRepository.findByGroupPurchaseIdAndUserId(GP_ID, USER_ID)).willReturn(Optional.of(existing));

        assertThatThrownBy(() -> participationService.join(GP_ID, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ConflictException.class);

        then(notificationService).should(never()).createParticipationJoined(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 공동구매 참여 시 ResourceNotFoundException")
    void join_공동구매없음_예외() {
        setSecurityContext(USER_ID);
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(mock(User.class)));
        given(groupPurchaseRepository.findById(GP_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> participationService.join(GP_ID, mock(JoinGroupPurchaseRequest.class)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ==================== cancel() ====================

    @Test
    @DisplayName("공동구매 참여 취소 시 PARTICIPATION_CANCELED 알림이 생성된다")
    void cancel_성공시_취소_알림_생성() {
        setSecurityContext(USER_ID);

        GroupPurchase gp = mock(GroupPurchase.class);
        given(gp.getTitle()).willReturn(GP_TITLE);

        GroupPurchaseParticipation participation = mock(GroupPurchaseParticipation.class);
        given(participation.getStatus()).willReturn(ParticipationStatus.ACTIVE);
        given(participation.getGroupPurchase()).willReturn(gp);

        given(participationRepository.findByGroupPurchaseIdAndUserId(GP_ID, USER_ID))
                .willReturn(Optional.of(participation));

        participationService.cancel(GP_ID);

        then(notificationService).should().createParticipationCanceled(USER_ID, GP_TITLE);
    }

    @Test
    @DisplayName("이미 취소된 참여에 대해 취소 시 ConflictException, 알림 미생성")
    void cancel_이미취소됨_예외_알림_미생성() {
        setSecurityContext(USER_ID);

        GroupPurchaseParticipation participation = mock(GroupPurchaseParticipation.class);
        given(participation.getStatus()).willReturn(ParticipationStatus.CANCELED);

        given(participationRepository.findByGroupPurchaseIdAndUserId(GP_ID, USER_ID))
                .willReturn(Optional.of(participation));

        assertThatThrownBy(() -> participationService.cancel(GP_ID))
                .isInstanceOf(ConflictException.class);

        then(notificationService).should(never()).createParticipationCanceled(any(), any());
    }

    @Test
    @DisplayName("참여 이력이 없는 공동구매 취소 시 ConflictException")
    void cancel_참여이력없음_예외() {
        setSecurityContext(USER_ID);
        given(participationRepository.findByGroupPurchaseIdAndUserId(GP_ID, USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> participationService.cancel(GP_ID))
                .isInstanceOf(ConflictException.class);
    }
}
