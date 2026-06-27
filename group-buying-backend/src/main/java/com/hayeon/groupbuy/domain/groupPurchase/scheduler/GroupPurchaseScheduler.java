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
    public void updateGroupPurchaseStatus() {

        // 1. RECRUITING 상태 처리
        List<GroupPurchase> recruitingList =
                groupPurchaseRepository.findAllByStatusAndDeleteDtIsNull(
                        GroupPurchaseStatus.RECRUITING
                );

        LocalDate now = LocalDate.now();

        for (GroupPurchase gp : recruitingList) {
            Long gpId = gp.getId();
            try {
                if (gp.getStartDt() != null && gp.getStartDt().isAfter(now)) {
                    continue;
                }
                boolean isEndedByTime = gp.getEndDt() != null && !gp.getEndDt().isAfter(now);
                if (isEndedByTime) {
                    processFailedGroupPurchase(gpId);
                    log.info("공동구매 만료 처리 id={}", gpId);
                }
            } catch (Exception e) {
                log.error("Scheduler RECRUITING 처리 실패 id={}, error={}", gpId, e.getMessage(), e);
            }
        }

        // 2. COMPLETED 상태 처리 (종료일 도달 시 주문 생성)
        List<GroupPurchase> completedList =
                groupPurchaseRepository.findAllByStatusAndDeleteDtIsNull(
                        GroupPurchaseStatus.COMPLETED
                );

        for (GroupPurchase gp : completedList) {
            Long gpId = gp.getId();
            try {
                boolean isEndedByTime = gp.getEndDt() != null && !gp.getEndDt().isAfter(now);
                if (isEndedByTime) {
                    processCompletedGroupPurchase(gpId);
                    log.info("공동구매 완료 처리 id={}", gpId);
                }
            } catch (Exception e) {
                log.error("Scheduler COMPLETED 처리 실패 id={}, error={}", gpId, e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void processFailedGroupPurchase(Long gpId) {
        GroupPurchase gp = groupPurchaseRepository.findById(gpId)
                .orElseThrow();
        gp.updateStatus(GroupPurchaseStatus.FAILED);
        groupPurchaseRepository.save(gp);
        eventPublisher.publishEvent(new GroupPurchaseClosedEvent(gpId, false));
    }

    @Transactional
    public void processCompletedGroupPurchase(Long gpId) {
        eventPublisher.publishEvent(new GroupPurchaseClosedEvent(gpId, true));
    }
}