package com.hayeon.groupbuy.domain.groupPurchase.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

@Repository
@RequiredArgsConstructor
@Profile("!test")
public class GroupPurchaseCountRedisRepository {

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<Long> joinScript;
    private final DefaultRedisScript<Long> cancelScript;

    public Long join(Long groupPurchaseId, Integer target) {
        return redisTemplate.execute(
                joinScript,
                Collections.singletonList(RedisKeyManager.countKey(groupPurchaseId)),
                String.valueOf(target)
        );
    }

    public Long cancel(Long groupPurchaseId) {
        return redisTemplate.execute(
                cancelScript,
                Collections.singletonList(RedisKeyManager.countKey(groupPurchaseId))
        );
    }

    public Long getCount(Long groupPurchaseId) {
        String key = RedisKeyManager.countKey(groupPurchaseId);
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    public void setCount(Long groupPurchaseId, Long count) {

        redisTemplate.opsForValue().set(
                RedisKeyManager.countKey(groupPurchaseId),
                String.valueOf(count)
        );
    }

    public long getCountOrDefault(Long groupPurchaseId) {
        String key = RedisKeyManager.countKey(groupPurchaseId);
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) return 0L;
            return Long.parseLong(value);
        } catch (Exception e) {
            // Redis 연결 실패 시 DB 값 fallback
            return 0L;
        }
    }
}