package com.hayeon.groupbuy.config;

import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestRedisStubConfig {

    @Bean
    public GroupPurchaseCountRedisRepository groupPurchaseCountRedisRepository() {
        // E2E 테스트에서는 Redis 기능을 사용하지 않으므로 빈 동작 객체로 대체
        return new GroupPurchaseCountRedisRepository(null, null, null) {
            @Override
            public Long join(Long groupPurchaseId, Integer target) {
                return 1L;
            }

            @Override
            public Long cancel(Long groupPurchaseId) {
                return 0L;
            }

            @Override
            public Long getCount(Long groupPurchaseId) {
                return 0L;
            }

            @Override
            public void setCount(Long groupPurchaseId, Long count) {
                // no-op
            }

            @Override
            public long getCountOrDefault(Long groupPurchaseId) {
                return 0L;
            }
        };
    }
}