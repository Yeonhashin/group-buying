package com.hayeon.groupbuy.domain.groupPurchase.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
@RequiredArgsConstructor
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

        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
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
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return 0L;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}