package com.aiw.backend.app.model.stt_state.service;

import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.app.model.stt_state.domain.SttState;
import com.aiw.backend.app.model.stt_state.dto.SttStateDTO;
import com.aiw.backend.app.model.stt_state.repos.SttStateRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SttStateService {

    private final SttStateRepository sttStateRepository;
    private final MeetingRepository meetingRepository;

    public SttStateService(final SttStateRepository sttStateRepository,
            final MeetingRepository meetingRepository) {
        this.sttStateRepository = sttStateRepository;
        this.meetingRepository = meetingRepository;
    }

    public List<SttStateDTO> findAll() {
        final List<SttState> sttStates = sttStateRepository.findAll(Sort.by("id"));
        return sttStates.stream()
                .map(sttState -> mapToDTO(sttState, new SttStateDTO()))
                .toList();
    }

    public SttStateDTO get(final Long id) {
        return sttStateRepository.findById(id)
                .map(sttState -> mapToDTO(sttState, new SttStateDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SttStateDTO sttStateDTO) {
        final SttState sttState = new SttState();
        mapToEntity(sttStateDTO, sttState);
        return sttStateRepository.save(sttState).getId();
    }

    public void update(final Long id, final SttStateDTO sttStateDTO) {
        final SttState sttState = sttStateRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sttStateDTO, sttState);
        sttStateRepository.save(sttState);
    }

    public void delete(final Long id) {
        final SttState sttState = sttStateRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        sttStateRepository.delete(sttState);
    }

    private SttStateDTO mapToDTO(final SttState sttState, final SttStateDTO sttStateDTO) {
        sttStateDTO.setId(sttState.getId());
        sttStateDTO.setState(sttState.getState());
        sttStateDTO.setLanguage(sttState.getLanguage());
        sttStateDTO.setRawJsonUrl(sttState.getRawJsonUrl());
        sttStateDTO.setStartedAt(sttState.getStartedAt());
        sttStateDTO.setEndedAt(sttState.getEndedAt());
        sttStateDTO.setErrorMessage(sttState.getErrorMessage());
        sttStateDTO.setActivated(sttState.getActivated());
        sttStateDTO.setMeeting(sttState.getMeeting() == null ? null : sttState.getMeeting().getId());
        return sttStateDTO;
    }

    private SttState mapToEntity(final SttStateDTO sttStateDTO, final SttState sttState) {
        sttState.setState(sttStateDTO.getState());
        sttState.setLanguage(sttStateDTO.getLanguage());
        sttState.setRawJsonUrl(sttStateDTO.getRawJsonUrl());
        sttState.setStartedAt(sttStateDTO.getStartedAt());
        sttState.setEndedAt(sttStateDTO.getEndedAt());
        sttState.setErrorMessage(sttStateDTO.getErrorMessage());
        sttState.setActivated(sttStateDTO.getActivated());
        final Meeting meeting = sttStateDTO.getMeeting() == null ? null : meetingRepository.findById(sttStateDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        sttState.setMeeting(meeting);
        return sttState;
    }

    public boolean rawJsonUrlExists(final String rawJsonUrl) {
        return sttStateRepository.existsByRawJsonUrlIgnoreCase(rawJsonUrl);
    }

    public boolean meetingExists(final Long id) {
        return sttStateRepository.existsByMeetingId(id);
    }

    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
        final SttState meetingSttState = sttStateRepository.findFirstByMeetingId(event.getId());
        if (meetingSttState != null) {
            referencedException.setKey("meeting.sttState.meeting.referenced");
            referencedException.addParam(meetingSttState.getId());
            throw referencedException;
        }
    }

}
