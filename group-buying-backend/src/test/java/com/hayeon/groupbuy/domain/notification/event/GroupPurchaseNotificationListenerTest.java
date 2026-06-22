package com.hayeon.groupbuy.domain.notification.event;

import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.notification.service.NotificationService;
import com.hayeon.groupbuy.domain.order.event.OrderPaidEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GroupPurchaseNotificationListenerTest {

    @InjectMocks
    private GroupPurchaseNotificationListener listener;

    @Mock private NotificationService notificationService;
    @Mock private GroupPurchaseParticipationRepository participationRepository;
    @Mock private GroupPurchaseRepository groupPurchaseRepository;

    @Test
    @DisplayName("OrderPaidEvent 수신 시 이벤트 데이터로 결제 완료 알림을 생성한다")
    void handle_OrderPaidEvent_이벤트_데이터로_알림_생성() {
        OrderPaidEvent event = new OrderPaidEvent(10L, 1L, "테스트 공동구매");

        listener.handle(event);

        then(notificationService).should().createOrderPaid(1L, "테스트 공동구매");
    }

    @Test
    @DisplayName("OrderPaidEvent 처리 시 불필요한 DB 조회를 하지 않는다")
    void handle_OrderPaidEvent_DB_조회_없음() {
        OrderPaidEvent event = new OrderPaidEvent(10L, 1L, "테스트 공동구매");

        listener.handle(event);

        verifyNoMoreInteractions(groupPurchaseRepository);
        verifyNoMoreInteractions(participationRepository);
    }
}
