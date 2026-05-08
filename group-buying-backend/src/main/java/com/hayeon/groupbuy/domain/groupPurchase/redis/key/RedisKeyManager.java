package com.hayeon.groupbuy.domain.groupPurchase.redis;

public class RedisKeyManager {

    private static final String PREFIX = "group_purchase";

    public static String countKey(Long groupPurchaseId) {
        return PREFIX + ":" + groupPurchaseId + ":count";
    }
}