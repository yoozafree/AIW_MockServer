package com.aiw.backend.notification.repos;

import com.aiw.backend.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findFirstByMemberId(Long id);

}
