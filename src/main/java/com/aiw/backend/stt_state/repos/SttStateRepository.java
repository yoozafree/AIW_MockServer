package com.aiw.backend.stt_state.repos;

import com.aiw.backend.stt_state.domain.SttState;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SttStateRepository extends JpaRepository<SttState, Long> {

    SttState findFirstByMeetingId(Long id);

    boolean existsByRawJsonUrlIgnoreCase(String rawJsonUrl);

    boolean existsByMeetingId(Long id);

}
