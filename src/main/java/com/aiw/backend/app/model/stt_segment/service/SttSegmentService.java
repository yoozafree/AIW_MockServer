package com.aiw.backend.app.model.stt_segment.service;

import com.aiw.backend.events.BeforeDeleteMeetingSpeakerMap;
import com.aiw.backend.app.model.meeting_speaker_map.domain.MeetingSpeakerMap;
import com.aiw.backend.app.model.meeting_speaker_map.repository.MeetingSpeakerMapRepository;
import com.aiw.backend.app.model.stt_segment.domain.SttSegment;
import com.aiw.backend.app.model.stt_segment.dto.SttSegmentDTO;
import com.aiw.backend.app.model.stt_segment.repository.SttSegmentRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SttSegmentService {

    private final SttSegmentRepository sttSegmentRepository;
    private final MeetingSpeakerMapRepository meetingSpeakerMapRepository;

    public SttSegmentService(final SttSegmentRepository sttSegmentRepository,
            final MeetingSpeakerMapRepository meetingSpeakerMapRepository) {
        this.sttSegmentRepository = sttSegmentRepository;
        this.meetingSpeakerMapRepository = meetingSpeakerMapRepository;
    }

    public List<SttSegmentDTO> findAll() {
        final List<SttSegment> sttSegments = sttSegmentRepository.findAll(Sort.by("id"));
        return sttSegments.stream()
                .map(sttSegment -> mapToDTO(sttSegment, new SttSegmentDTO()))
                .toList();
    }

    public SttSegmentDTO get(final Long id) {
        return sttSegmentRepository.findById(id)
                .map(sttSegment -> mapToDTO(sttSegment, new SttSegmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SttSegmentDTO sttSegmentDTO) {
        final SttSegment sttSegment = new SttSegment();
        mapToEntity(sttSegmentDTO, sttSegment);
        return sttSegmentRepository.save(sttSegment).getId();
    }

    public void update(final Long id, final SttSegmentDTO sttSegmentDTO) {
        final SttSegment sttSegment = sttSegmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sttSegmentDTO, sttSegment);
        sttSegmentRepository.save(sttSegment);
    }

    public void delete(final Long id) {
        final SttSegment sttSegment = sttSegmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        sttSegmentRepository.delete(sttSegment);
    }

    private SttSegmentDTO mapToDTO(final SttSegment sttSegment, final SttSegmentDTO sttSegmentDTO) {
        sttSegmentDTO.setId(sttSegment.getId());
        sttSegmentDTO.setSpeakerLabel(sttSegment.getSpeakerLabel());
        sttSegmentDTO.setStartMs(sttSegment.getStartMs());
        sttSegmentDTO.setEndMs(sttSegment.getEndMs());
        sttSegmentDTO.setSegText(sttSegment.getSegText());
        sttSegmentDTO.setMeetingSpeakerMap(sttSegment.getMeetingSpeakerMap() == null ? null : sttSegment.getMeetingSpeakerMap().getId());
        return sttSegmentDTO;
    }

    private SttSegment mapToEntity(final SttSegmentDTO sttSegmentDTO, final SttSegment sttSegment) {
        sttSegment.setSpeakerLabel(sttSegmentDTO.getSpeakerLabel());
        sttSegment.setStartMs(sttSegmentDTO.getStartMs());
        sttSegment.setEndMs(sttSegmentDTO.getEndMs());
        sttSegment.setSegText(sttSegmentDTO.getSegText());
        final MeetingSpeakerMap meetingSpeakerMap = sttSegmentDTO.getMeetingSpeakerMap() == null ? null : meetingSpeakerMapRepository.findById(sttSegmentDTO.getMeetingSpeakerMap())
                .orElseThrow(() -> new NotFoundException("meetingSpeakerMap not found"));
        sttSegment.setMeetingSpeakerMap(meetingSpeakerMap);
        return sttSegment;
    }

    public boolean segTextExists(final String segText) {
        return sttSegmentRepository.existsBySegText(segText);
    }

    @EventListener(BeforeDeleteMeetingSpeakerMap.class)
    public void on(final BeforeDeleteMeetingSpeakerMap event) {
        final ReferencedException referencedException = new ReferencedException();
        final SttSegment meetingSpeakerMapSttSegment = sttSegmentRepository.findFirstByMeetingSpeakerMapId(event.getId());
        if (meetingSpeakerMapSttSegment != null) {
            referencedException.setKey("meetingSpeakerMap.sttSegment.meetingSpeakerMap.referenced");
            referencedException.addParam(meetingSpeakerMapSttSegment.getId());
            throw referencedException;
        }
    }

}
