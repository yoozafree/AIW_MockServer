package com.aiw.backend.app.model.stt_segment.repository;

import com.aiw.backend.app.model.stt_segment.domain.SttSegment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SttSegmentRepository extends JpaRepository<SttSegment, Long> {

    SttSegment findFirstByMeetingSpeakerMapId(Long id);

    boolean existsBySegText(String segText);

}
