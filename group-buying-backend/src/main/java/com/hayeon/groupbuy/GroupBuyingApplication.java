package com.hayeon.groupbuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.hayeon.groupbuy")
@EnableScheduling
@EntityScan(basePackages = "com.hayeon.groupbuy.domain")
public class GroupBuyingApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupBuyingApplication.class, args);
    }
}