package com.aiw.backend.meeting.repos;

import com.aiw.backend.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
