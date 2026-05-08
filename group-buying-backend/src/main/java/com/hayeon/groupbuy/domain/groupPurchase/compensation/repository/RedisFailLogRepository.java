package com.hayeon.groupbuy.domain.groupPurchase.compensation.repository;

import com.hayeon.groupbuy.domain.groupPurchase.compensation.entity.RedisFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedisFailLogRepository extends JpaRepository<RedisFailLog, Long> {

    List<RedisFailLog> findTop100ByOrderByIdAsc();
}