package com.hayeon.groupbuy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RedisLuaConfig {

    @Bean
    public DefaultRedisScript<Long> joinScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/lua/join.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> cancelScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/lua/cancel.lua"));
        script.setResultType(Long.class);
        return script;
    }
}