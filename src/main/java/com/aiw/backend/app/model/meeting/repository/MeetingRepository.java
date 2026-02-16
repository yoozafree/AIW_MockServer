package com.aiw.backend.app.model.meeting.repository;

import com.aiw.backend.app.model.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    //AI 데일리 피드백: 전체 회의 중 오늘 회의 목록 조회
    List<Meeting> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
