package com.aiw.backend.stt_segment.repos;

import com.aiw.backend.stt_segment.domain.SttSegment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SttSegmentRepository extends JpaRepository<SttSegment, Long> {

    SttSegment findFirstByMeetingSpeakerMapId(Long id);

    boolean existsBySegText(String segText);

}
