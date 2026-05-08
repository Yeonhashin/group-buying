package com.hayeon.groupbuy.domain.notification.repository;

import com.hayeon.groupbuy.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreateDtDesc(Long userId);

    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
}