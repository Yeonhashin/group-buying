package com.hayeon.groupbuy.domain.groupPurchase.service;

import com.hayeon.groupbuy.domain.groupPurchase.entity.GroupPurchase;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.GroupPurchaseParticipation;
import com.hayeon.groupbuy.domain.groupPurchase.compensation.entity.RedisFailLog;

import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.hayeon.groupbuy.domain.groupPurchase.repository.GroupPurchaseParticipationRepository;
import com.hayeon.groupbuy.domain.groupPurchase.dto.request.JoinGroupPurchaseRequest;
import com.hayeon.groupbuy.domain.groupPurchase.participation.entity.ParticipationStatus;
import com.hayeon.groupbuy.domain.groupPurchase.compensation.repository.RedisFailLogRepository;
import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import com.hayeon.groupbuy.domain.groupPurchase.enums.GroupPurchaseStatus;

import com.hayeon.groupbuy.domain.user.entity.User;
import com.hayeon.groupbuy.domain.user.repository.UserRepository;
import com.hayeon.groupbuy.global.exception.ConflictException;
import com.hayeon.groupbuy.global.exception.ResourceNotFoundException;
import com.hayeon.groupbuy.global.exception.UnauthorizedException;
import com.hayeon.groupbuy.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPurchaseParticipationService {
    private final GroupPurchaseParticipationRepository groupPurchaseParticipationRepository;
    private final GroupPurchaseCountRedisRepository groupPurchaseCountRedisRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final UserRepository userRepository;
    private final RedisFailLogRepository redisFailLogRepository;

    @Transactional
    public void join(Long id, JoinGroupPurchaseRequest request) {

        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 유저입니다."));

        GroupPurchase groupPurchase = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("참여할 공동구매가 존재하지 않습니다."));

        // 참여 가능 여부 체크
        if (groupPurchase.getDeleteDt() != null ||
                groupPurchase.getStatus() != GroupPurchaseStatus.RECRUITING ||
                groupPurchase.getStartDt().isAfter(LocalDate.now()) ||
                groupPurchase.getEndDt().isBefore(LocalDate.now())) {

            throw new ResourceNotFoundException("참여 불가능한 공동구매입니다.");
        }

        GroupPurchaseParticipation participation =
                groupPurchaseParticipationRepository
                        .findByGroupPurchaseIdAndUserId(id, userId)
                        .orElse(null);

        if (participation != null &&
                participation.getStatus() == ParticipationStatus.ACTIVE) {
            throw new ConflictException("이미 참여한 공동구매입니다.");
        }

        // 🔥 Redis 먼저 (단순화)
        Long currentCount = groupPurchaseCountRedisRepository
                .join(id, groupPurchase.getTargetParticipants());

        if (currentCount == null || currentCount == -1) {
            throw new ConflictException("참여 인원이 초과되었습니다.");
        }

        try {

            if (participation != null) {
                // 재참여
                participation.setStatus(ParticipationStatus.ACTIVE);
                participation.setQuantity(1); // 고정
            } else {
                // 신규
                participation = GroupPurchaseParticipation.builder()
                        .groupPurchase(groupPurchase)
                        .user(user)
                        .quantity(1)
                        .status(ParticipationStatus.ACTIVE)
                        .build();
            }

            groupPurchaseParticipationRepository.save(participation);

        } catch (Exception e) {

            // ❗ Redis 롤백
            groupPurchaseCountRedisRepository.cancel(id);

            redisFailLogRepository.save(
                    RedisFailLog.builder()
                            .groupPurchaseId(id)
                            .userId(userId)
                            .action("INCR_ROLLBACK")
                            .build()
            );

            throw new RuntimeException("참여 처리 실패");
        }

        if (currentCount.equals(groupPurchase.getTargetParticipants())) {
            groupPurchase.setStatus(GroupPurchaseStatus.COMPLETED);
            groupPurchaseRepository.save(groupPurchase);
        }
    }

    @Transactional
    public void cancel(Long id) {

        Long userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("로그인이 필요합니다."));

        GroupPurchaseParticipation participation =
                groupPurchaseParticipationRepository
                        .findByGroupPurchaseIdAndUserId(id, userId)
                        .orElseThrow(() -> new ConflictException("참여한 공동구매가 존재하지 않습니다."));

        if (participation.getStatus() != ParticipationStatus.ACTIVE) {
            throw new ConflictException("이미 취소된 공동구매입니다.");
        }

        participation.setStatus(ParticipationStatus.CANCELED);

        try {
            groupPurchaseCountRedisRepository.cancel(id);
        } catch (Exception e) {
            log.error("Redis DECR 실패", e);

            redisFailLogRepository.save(
                    RedisFailLog.builder()
                            .groupPurchaseId(id)
                            .userId(userId)
                            .action("DECR")
                            .build()
            );
        }
    }
}