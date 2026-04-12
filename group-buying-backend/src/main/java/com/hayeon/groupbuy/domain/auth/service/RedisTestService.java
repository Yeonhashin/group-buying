package com.hayeon.groupbuy.domain.auth.service;

import org.springframework.stereotype.Service;

import org.springframework.data.redis.core.RedisTemplate;

@Service
public class RedisTestService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTestService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void test() {
        System.out.println("1. 시작");

        redisTemplate.opsForValue().set("test", "hello");

        System.out.println("2. 저장 완료");

        Object value = redisTemplate.opsForValue().get("test");
        System.out.println("3. 조회: " + value);
    }
}