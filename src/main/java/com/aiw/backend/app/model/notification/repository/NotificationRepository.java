package com.aiw.backend.app.model.notification.repository;

import com.aiw.backend.app.model.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findFirstByMemberId(Long id);

}
