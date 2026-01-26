package com.aiw.backend.app.model.meeting_speaker_map.service;

import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.events.BeforeDeleteMeetingSpeakerMap;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.app.model.meeting_speaker_map.domain.MeetingSpeakerMap;
import com.aiw.backend.app.model.meeting_speaker_map.model.MeetingSpeakerMapDTO;
import com.aiw.backend.app.model.meeting_speaker_map.repository.MeetingSpeakerMapRepository;
import com.aiw.backend.util.CustomCollectors;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MeetingSpeakerMapService {

    private final MeetingSpeakerMapRepository meetingSpeakerMapRepository;
    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher publisher;

    public MeetingSpeakerMapService(final MeetingSpeakerMapRepository meetingSpeakerMapRepository,
            final MeetingRepository meetingRepository, final ApplicationEventPublisher publisher) {
        this.meetingSpeakerMapRepository = meetingSpeakerMapRepository;
        this.meetingRepository = meetingRepository;
        this.publisher = publisher;
    }

    public List<MeetingSpeakerMapDTO> findAll() {
        final List<MeetingSpeakerMap> meetingSpeakerMaps = meetingSpeakerMapRepository.findAll(Sort.by("id"));
        return meetingSpeakerMaps.stream()
                .map(meetingSpeakerMap -> mapToDTO(meetingSpeakerMap, new MeetingSpeakerMapDTO()))
                .toList();
    }

    public MeetingSpeakerMapDTO get(final Long id) {
        return meetingSpeakerMapRepository.findById(id)
                .map(meetingSpeakerMap -> mapToDTO(meetingSpeakerMap, new MeetingSpeakerMapDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
        final MeetingSpeakerMap meetingSpeakerMap = new MeetingSpeakerMap();
        mapToEntity(meetingSpeakerMapDTO, meetingSpeakerMap);
        return meetingSpeakerMapRepository.save(meetingSpeakerMap).getId();
    }

    public void update(final Long id, final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
        final MeetingSpeakerMap meetingSpeakerMap = meetingSpeakerMapRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(meetingSpeakerMapDTO, meetingSpeakerMap);
        meetingSpeakerMapRepository.save(meetingSpeakerMap);
    }

    public void delete(final Long id) {
        final MeetingSpeakerMap meetingSpeakerMap = meetingSpeakerMapRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteMeetingSpeakerMap(id));
        meetingSpeakerMapRepository.delete(meetingSpeakerMap);
    }

    private MeetingSpeakerMapDTO mapToDTO(final MeetingSpeakerMap meetingSpeakerMap,
            final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
        meetingSpeakerMapDTO.setId(meetingSpeakerMap.getId());
        meetingSpeakerMapDTO.setSpeakerLabel(meetingSpeakerMap.getSpeakerLabel());
        meetingSpeakerMapDTO.setMeeting(meetingSpeakerMap.getMeeting() == null ? null : meetingSpeakerMap.getMeeting().getId());
        return meetingSpeakerMapDTO;
    }

    private MeetingSpeakerMap mapToEntity(final MeetingSpeakerMapDTO meetingSpeakerMapDTO,
            final MeetingSpeakerMap meetingSpeakerMap) {
        meetingSpeakerMap.setSpeakerLabel(meetingSpeakerMapDTO.getSpeakerLabel());
        final Meeting meeting = meetingSpeakerMapDTO.getMeeting() == null ? null : meetingRepository.findById(meetingSpeakerMapDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        meetingSpeakerMap.setMeeting(meeting);
        return meetingSpeakerMap;
    }

    public Map<Long, String> getMeetingSpeakerMapValues() {
        return meetingSpeakerMapRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(MeetingSpeakerMap::getId, MeetingSpeakerMap::getSpeakerLabel));
    }

    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
        final MeetingSpeakerMap meetingMeetingSpeakerMap = meetingSpeakerMapRepository.findFirstByMeetingId(event.getId());
        if (meetingMeetingSpeakerMap != null) {
            referencedException.setKey("meeting.meetingSpeakerMap.meeting.referenced");
            referencedException.addParam(meetingMeetingSpeakerMap.getId());
            throw referencedException;
        }
    }

}
