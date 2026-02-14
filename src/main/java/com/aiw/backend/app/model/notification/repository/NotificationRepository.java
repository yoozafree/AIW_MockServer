package com.aiw.backend.app.model.notification.repository;

import com.aiw.backend.app.model.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 마이페이지용
    Notification findFirstByMemberId(Long id);

    // 대시보드용
    // 특정 사용자의 알림 목록 조회 (최신순)
    List<Notification> findByMemberIdAndTypeIsNotOrderByCreatedAtDesc(Long memberId, String excludeType);

    // 특정 팀의 알림 목록
    List<Notification> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    // 특정 타입의 알림 목록
    List<Notification> findByMemberIdAndTypeOrderByCreatedAtDesc(Long memberId, String type);

}
