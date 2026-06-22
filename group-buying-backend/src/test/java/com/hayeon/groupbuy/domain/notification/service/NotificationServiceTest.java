package com.hayeon.groupbuy.domain.notification.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    private static final Long USER_ID = 1L;
    private static final String GP_TITLE = "테스트 공동구매";

    // ==================== createParticipationJoined() ====================

    @Test
    @DisplayName("참여 알림 생성 시 PARTICIPATION_JOINED 상태로 저장된다")
    void createParticipationJoined_올바른_상태로_저장() {
        given(notificationRepository.saveAndFlush(any())).willAnswer(inv -> inv.getArgument(0));

        notificationService.createParticipationJoined(USER_ID, GP_TITLE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PARTICIPATION_JOINED);
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("참여 알림 메시지에 공동구매 제목이 포함된다")
    void createParticipationJoined_메시지에_제목_포함() {
        given(notificationRepository.saveAndFlush(any())).willAnswer(inv -> inv.getArgument(0));

        notificationService.createParticipationJoined(USER_ID, GP_TITLE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());

        assertThat(captor.getValue().getMessage()).contains(GP_TITLE);
    }

    // ==================== createParticipationCanceled() ====================

    @Test
    @DisplayName("취소 알림 생성 시 PARTICIPATION_CANCELED 상태로 저장된다")
    void createParticipationCanceled_올바른_상태로_저장() {
        given(notificationRepository.saveAndFlush(any())).willAnswer(inv -> inv.getArgument(0));

        notificationService.createParticipationCanceled(USER_ID, GP_TITLE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PARTICIPATION_CANCELED);
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("취소 알림 메시지에 공동구매 제목이 포함된다")
    void createParticipationCanceled_메시지에_제목_포함() {
        given(notificationRepository.saveAndFlush(any())).willAnswer(inv -> inv.getArgument(0));

        notificationService.createParticipationCanceled(USER_ID, GP_TITLE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().saveAndFlush(captor.capture());

        assertThat(captor.getValue().getMessage()).contains(GP_TITLE);
    }
}
