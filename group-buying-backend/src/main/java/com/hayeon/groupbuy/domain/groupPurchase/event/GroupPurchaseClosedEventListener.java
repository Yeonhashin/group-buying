package com.hayeon.groupbuy.domain.groupPurchase.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseClosedEventListener {

    /**
     * 이벤트 로깅 전용 Listener
     * - 비즈니스 로직 절대 넣지 말 것
     * - 모니터링/추적 용도
     */

    @Order(0) // 가장 먼저 실행되도록 (선택)
    @EventListener
    public void handle(GroupPurchaseClosedEvent event) {

        Long gpId = event.getGroupPurchaseId();
        boolean success = event.isSuccess();

        if (success) {
            log.info("[EVENT] 공동구매 성공 종료 gpId={}", gpId);
        } else {
            log.info("[EVENT] 공동구매 실패 종료 gpId={}", gpId);
        }
    }
}