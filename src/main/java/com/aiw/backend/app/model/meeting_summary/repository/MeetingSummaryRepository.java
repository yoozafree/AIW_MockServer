package com.aiw.backend.app.model.meeting_summary.repository;

import com.aiw.backend.app.model.meeting_summary.domain.MeetingSummary;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingSummaryRepository extends JpaRepository<MeetingSummary, Long> {

    MeetingSummary findFirstByMeetingId(Long id);

    boolean existsBySummaryText(String summaryText);

    boolean existsByKeyDecisionIgnoreCase(String keyDecision);

    boolean existsByMeetingId(Long id);

}
