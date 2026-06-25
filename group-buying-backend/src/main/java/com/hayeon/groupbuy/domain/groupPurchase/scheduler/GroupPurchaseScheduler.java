package com.hayeon.groupbuy.domain.groupPurchase.scheduler;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseScheduler {

    private final GroupPurchaseRepository groupPurchaseRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 공동구매 만료 처리 스케줄러
     * - 60초마다 실행
     * - 종료일이 지난 RECRUITING 상태를 FAILED로 변경
     * - COMPLETED 처리는 join() 에서 즉시 처리
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void updateGroupPurchaseStatus() {

        List<GroupPurchase> list =
                groupPurchaseRepository.findAllByStatusAndDeleteDtIsNull(
                        GroupPurchaseStatus.RECRUITING
                );

        LocalDate now = LocalDate.now();

        for (GroupPurchase gp : list) {

            Long gpId = gp.getId();

            try {
                // 시작 전이면 skip
                if (gp.getStartDt() != null && gp.getStartDt().isAfter(now)) {
                    continue;
                }

                // 종료일이 지난 경우 FAILED 처리
                boolean isEndedByTime = gp.getEndDt() != null && !gp.getEndDt().isAfter(now);

                if (isEndedByTime) {
                    gp.updateStatus(GroupPurchaseStatus.FAILED);
                    groupPurchaseRepository.save(gp);
                    eventPublisher.publishEvent(new GroupPurchaseClosedEvent(gpId, false));
                    log.info("공동구매 만료 처리 id={}", gpId);
                }

            } catch (Exception e) {
                log.error("Scheduler 처리 실패 id={}, error={}", gpId, e.getMessage(), e);
            }
        }
    }
}