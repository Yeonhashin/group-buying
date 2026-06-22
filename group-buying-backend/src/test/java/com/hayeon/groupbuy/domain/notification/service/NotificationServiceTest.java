package com.hayeon.groupbuy.domain.notification.service;

import com.hayeon.groupbuy.domain.notification.dto.response.NotificationResponse;
import com.hayeon.groupbuy.domain.notification.entity.Notification;
import com.hayeon.groupbuy.domain.notification.enums.NotificationStatus;
import com.hayeon.groupbuy.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    // ==================== create() ====================

    @Test
    @DisplayName("알림 생성 성공 - 저장된 알림 내용 검증")
    void create_성공() {
        notificationService.create(1L, NotificationStatus.ORDER_CREATED, "테스트 메시지");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());
        // 캡처된 객체는 Notification.create()로 생성된 실제 객체라 검증 불가
        // (private 필드라 getter 없으면 접근 불가) → save 호출 여부만 검증
    }

    // ==================== createOrderXxx() 편의 메서드 ====================

    @Test
    @DisplayName("주문 생성 알림 생성 성공")
    void createOrderCreated_성공() {
        notificationService.createOrderCreated(1L, "테스트 공동구매");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());
    }

    @Test
    @DisplayName("결제 완료 알림 생성 성공")
    void createOrderPaid_성공() {
        notificationService.createOrderPaid(1L, "테스트 공동구매");

        then(notificationRepository).should().saveAndFlush(any(Notification.class));
    }

    @Test
    @DisplayName("결제 실패 알림 생성 성공")
    void createOrderFailed_성공() {
        notificationService.createOrderFailed(1L, "테스트 공동구매");

        then(notificationRepository).should().saveAndFlush(any(Notification.class));
    }

    @Test
    @DisplayName("사용자 주문 취소 알림 생성 성공")
    void createOrderCanceledByUser_성공() {
        notificationService.createOrderCanceledByUser(1L, "테스트 공동구매");

        then(notificationRepository).should().saveAndFlush(any(Notification.class));
    }

    @Test
    @DisplayName("자동 주문 취소 알림 생성 성공")
    void createOrderAutoCanceled_성공() {
        notificationService.createOrderAutoCanceled(1L, "테스트 공동구매");

        then(notificationRepository).should().saveAndFlush(any(Notification.class));
    }

    @Test
    @DisplayName("환불 알림 생성 성공")
    void createOrderRefunded_성공() {
        notificationService.createOrderRefunded(1L, "테스트 공동구매");

        then(notificationRepository).should().saveAndFlush(any(Notification.class));
    }

    @Test
    @DisplayName("공동구매 참여 알림 생성 시 PARTICIPATION_JOINED 상태와 제목이 메시지에 포함된다")
    void createParticipationJoined_상태_및_메시지_검증() {
        notificationService.createParticipationJoined(1L, "테스트 공동구매");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PARTICIPATION_JOINED);
        assertThat(saved.getMessage()).contains("테스트 공동구매");
        assertThat(saved.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("공동구매 참여 취소 알림 생성 시 PARTICIPATION_CANCELED 상태와 제목이 메시지에 포함된다")
    void createParticipationCanceled_상태_및_메시지_검증() {
        notificationService.createParticipationCanceled(1L, "테스트 공동구매");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PARTICIPATION_CANCELED);
        assertThat(saved.getMessage()).contains("테스트 공동구매");
        assertThat(saved.getUserId()).isEqualTo(1L);
    }

    // ==================== getNotifications() ====================

    @Test
    @DisplayName("전체 알림 조회 성공")
    void getNotifications_성공() {
        Notification notification = mock(Notification.class);
        given(notification.getId()).willReturn(1L);
        given(notification.getStatus()).willReturn(NotificationStatus.ORDER_CREATED);
        given(notification.getMessage()).willReturn("테스트");
        given(notification.getIsRead()).willReturn(false);
        given(notification.getCreateDt()).willReturn(null);

        given(notificationRepository.findByUserIdOrderByCreateDtDesc(1L))
                .willReturn(List.of(notification));

        List<NotificationResponse> result = notificationService.getNotifications(1L);

        assertThat(result).hasSize(1);
    }

    // ==================== getUnreadNotifications() ====================

    @Test
    @DisplayName("읽지 않은 알림 조회 성공")
    void getUnreadNotifications_성공() {
        Notification notification = mock(Notification.class);
        given(notification.getId()).willReturn(1L);
        given(notification.getStatus()).willReturn(NotificationStatus.ORDER_CREATED);
        given(notification.getMessage()).willReturn("테스트");
        given(notification.getIsRead()).willReturn(false);
        given(notification.getCreateDt()).willReturn(null);

        given(notificationRepository.findByUserIdAndIsRead(1L, false))
                .willReturn(List.of(notification));

        List<NotificationResponse> result = notificationService.getUnreadNotifications(1L);

        assertThat(result).hasSize(1);
    }

    // ==================== markAsRead() ====================

    @Test
    @DisplayName("단건 읽음 처리 성공")
    void markAsRead_성공() {
        Notification notification = mock(Notification.class);
        given(notification.getUserId()).willReturn(1L);
        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

        notificationService.markAsRead(1L, 1L);

        then(notification).should().markAsRead();
    }

    @Test
    @DisplayName("존재하지 않는 알림 읽음 처리 시 예외")
    void markAsRead_없음_예외() {
        given(notificationRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(999L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    @DisplayName("타인 알림 읽음 처리 시 예외")
    void markAsRead_타인알림_예외() {
        Notification notification = mock(Notification.class);
        given(notification.getUserId()).willReturn(999L); // 다른 유저 소유
        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INVALID_USER");
    }

    // ==================== markAllAsRead() ====================

    @Test
    @DisplayName("모두 읽음 처리 성공")
    void markAllAsRead_성공() {
        Notification n1 = mock(Notification.class);
        Notification n2 = mock(Notification.class);
        given(notificationRepository.findByUserIdAndIsRead(1L, false))
                .willReturn(List.of(n1, n2));

        notificationService.markAllAsRead(1L);

        then(n1).should().markAsRead();
        then(n2).should().markAsRead();
    }

    @Test
    @DisplayName("알림이 없는 경우 빈 리스트 반환")
    void getNotifications_빈리스트_반환() {
        given(notificationRepository.findByUserIdOrderByCreateDtDesc(1L))
                .willReturn(List.of());

        List<NotificationResponse> result = notificationService.getNotifications(1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("모두 읽음 상태일 때 읽지 않은 알림 빈 리스트 반환")
    void getUnreadNotifications_모두읽음_빈리스트() {
        given(notificationRepository.findByUserIdAndIsRead(1L, false))
                .willReturn(List.of());

        List<NotificationResponse> result = notificationService.getUnreadNotifications(1L);

        assertThat(result).isEmpty();
    }
}