package com.aiw.backend.meeting_speaker_map.repos;

import com.aiw.backend.meeting_speaker_map.domain.MeetingSpeakerMap;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingSpeakerMapRepository extends JpaRepository<MeetingSpeakerMap, Long> {

    MeetingSpeakerMap findFirstByMeetingId(Long id);

}
