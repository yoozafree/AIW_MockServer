package com.aiw.backend.meeting_participant.repos;

import com.aiw.backend.meeting_participant.domain.MeetingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    MeetingParticipant findFirstByMemberId(Long id);

    MeetingParticipant findFirstByMeetingId(Long id);

}
