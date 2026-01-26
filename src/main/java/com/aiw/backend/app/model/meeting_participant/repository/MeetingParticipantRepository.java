package com.aiw.backend.app.model.meeting_participant.repository;

import com.aiw.backend.app.model.meeting_participant.domain.MeetingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    MeetingParticipant findFirstByMemberId(Long id);

    MeetingParticipant findFirstByMeetingId(Long id);

}
