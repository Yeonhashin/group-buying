package com.hayeon.groupbuy.domain.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hayeon.groupbuy.domain.auth.service.RedisTestService;

@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    private final RedisTestService redisTestService;

    public RedisTestController(RedisTestService redisTestService) {
        this.redisTestService = redisTestService;
    }

    @GetMapping("/test")
    public String test() {
        redisTestService.test();
        return "ok";
    }
}