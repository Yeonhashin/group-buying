package com.hayeon.groupbuy.domain.groupPurchase.compensation.scheduler;

import com.hayeon.groupbuy.domain.groupPurchase.compensation.entity.RedisFailLog;
import com.hayeon.groupbuy.domain.groupPurchase.compensation.repository.RedisFailLogRepository;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCompensationScheduler {

    private final RedisFailLogRepository logRepository;
    private final GroupPurchaseCountRedisRepository redisRepository;
    private final GroupPurchaseParticipationRepository participationRepository;

    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void compensate() {

        List<RedisFailLog> logs = logRepository.findTop100ByOrderByIdAsc();

        for (RedisFailLog logEntity : logs) {

            try {
                Long gpId = logEntity.getGroupPurchaseId();

                // DB 기준 count 재계산
                long count = participationRepository.countByGroupPurchaseIdAndStatus(
                        gpId,
                        ParticipationStatus.ACTIVE
                );

                // Redis 재세팅
                redisRepository.setCount(gpId, count);

                // 로그 삭제
                logRepository.delete(logEntity);

                log.info("Redis 보정 완료 gpId={}, count={}", gpId, count);

            } catch (Exception e) {
                log.error("Redis 보정 실패 id={}", logEntity.getId(), e);
            }
        }
    }
}