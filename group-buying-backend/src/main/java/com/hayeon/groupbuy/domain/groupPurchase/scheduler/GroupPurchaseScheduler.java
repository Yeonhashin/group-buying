package com.hayeon.groupbuy.domain.groupPurchase.scheduler;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;
import com.hayeon.groupbuy.domain.groupPurchase.event.GroupPurchaseClosedEvent;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;

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
    private final GroupPurchaseCountRedisRepository redisRepository;
    private final GroupPurchaseParticipationRepository participationRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 공동구매 상태 업데이트 스케줄러
     * - 10초마다 실행
     * - RECRUITING 상태만 조회
     * - 종료 조건 만족 시 상태 변경 + 이벤트 발행
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void updateGroupPurchaseStatus() {

        List<GroupPurchase> list =
                groupPurchaseRepository.findAllByStatusAndDeleteDtIsNull(
                        GroupPurchaseStatus.RECRUITING
                );

        for (GroupPurchase gp : list) {

            Long gpId = gp.getId();

            try {
                // 1. Redis count 조회
                Long count = redisRepository.getCount(gpId);

                // 2. Redis 없으면 DB fallback
                if (count == null) {
                    count = participationRepository.countByGroupPurchaseIdAndStatus(
                            gpId,
                            ParticipationStatus.ACTIVE
                    );
                    redisRepository.setCount(gpId, count);
                }

                // 3. 시작 전이면 skip
                LocalDate now = LocalDate.now();

                boolean notStarted = gp.getStartDt() != null
                        && gp.getStartDt().isAfter(now);

                if (notStarted) {
                    continue;
                }

                // 4. 종료 조건 체크
                boolean isEndedByTime = gp.getEndDt() != null
                        && !gp.getEndDt().isAfter(now);

                boolean isStarted = gp.getStartDt() == null
                        || !gp.getStartDt().isAfter(now);

                if (!isStarted) continue;

                boolean isFull = count >= gp.getTargetParticipants();

                if (isEndedByTime || isFull) {

                    // 5. 성공 여부 판단
                    boolean isSuccess = isFull;

                    // 6. 상태 변경
                    gp.updateStatus(
                            isSuccess
                                    ? GroupPurchaseStatus.COMPLETED
                                    : GroupPurchaseStatus.FAILED
                    );

                    groupPurchaseRepository.save(gp);

                    // 7. 이벤트 발행
                    eventPublisher.publishEvent(
                            new GroupPurchaseClosedEvent(gpId, isSuccess)
                    );
                    log.info("이벤트 발행 완료");
                    log.info("공동구매 종료 처리 id={}, success={}, count={}", gpId, isSuccess, count);
                }

            } catch (Exception e) {
                log.error(
                        "Scheduler 처리 실패 id={}, error={}",
                        gpId,
                        e.getMessage(),
                        e
                );
            }
        }
    }
}