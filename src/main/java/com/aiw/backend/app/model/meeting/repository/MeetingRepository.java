package com.aiw.backend.app.model.meeting.repos;

import com.aiw.backend.app.model.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
