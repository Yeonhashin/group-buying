package com.hayeon.groupbuy.domain.notification.event;

import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseNotificationListener {

    @EventListener
    public void handle(GroupPurchaseClosedEvent event) {

        if (event.isSuccess()) {
            log.info("공동구매 성공 알림 발송 id={}", event.getGroupPurchaseId());
        } else {
            log.info("공동구매 실패 알림 발송 id={}", event.getGroupPurchaseId());
        }

        // TODO: NotificationService 연결
    }
}