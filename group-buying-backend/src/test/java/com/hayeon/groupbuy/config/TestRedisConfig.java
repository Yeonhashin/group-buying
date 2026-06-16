package com.hayeon.groupbuy.config;

import com.hayeon.groupbuy.domain.groupPurchase.redis.GroupPurchaseCountRedisRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestRedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // mock 대신 실제 Lettuce factory 사용 (연결은 안 해도 Bean 자체는 필요)
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public DefaultRedisScript<Long> joinScript() {
        return mock(DefaultRedisScript.class);
    }

    @Bean
    public DefaultRedisScript<Long> cancelScript() {
        return mock(DefaultRedisScript.class);
    }

    @Bean
    public GroupPurchaseCountRedisRepository groupPurchaseCountRedisRepository() {
        return mock(GroupPurchaseCountRedisRepository.class);
    }
}