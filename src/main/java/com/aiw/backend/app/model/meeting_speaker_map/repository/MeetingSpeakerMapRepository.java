package com.aiw.backend.app.model.meeting_speaker_map.repository;

import com.aiw.backend.app.model.meeting_speaker_map.domain.MeetingSpeakerMap;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MeetingSpeakerMapRepository extends JpaRepository<MeetingSpeakerMap, Long> {

  MeetingSpeakerMap findFirstByMeetingId(Long id);

  boolean existsByIdIgnoreCase(String id);

}
