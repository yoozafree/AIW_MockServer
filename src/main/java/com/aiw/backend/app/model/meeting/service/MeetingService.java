package com.aiw.backend.app.model.meeting.service;

import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.model.MeetingDTO;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.util.CustomCollectors;
import com.aiw.backend.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher publisher;

    public MeetingService(final MeetingRepository meetingRepository,
            final ApplicationEventPublisher publisher) {
        this.meetingRepository = meetingRepository;
        this.publisher = publisher;
    }

    public List<MeetingDTO> findAll() {
        final List<Meeting> meetings = meetingRepository.findAll(Sort.by("id"));
        return meetings.stream()
                .map(meeting -> mapToDTO(meeting, new MeetingDTO()))
                .toList();
    }

    public MeetingDTO get(final Long id) {
        return meetingRepository.findById(id)
                .map(meeting -> mapToDTO(meeting, new MeetingDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MeetingDTO meetingDTO) {
        final Meeting meeting = new Meeting();
        mapToEntity(meetingDTO, meeting);
        return meetingRepository.save(meeting).getId();
    }

    public void update(final Long id, final MeetingDTO meetingDTO) {
        final Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(meetingDTO, meeting);
        meetingRepository.save(meeting);
    }

    public void delete(final Long id) {
        final Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteMeeting(id));
        meetingRepository.delete(meeting);
    }

    private MeetingDTO mapToDTO(final Meeting meeting, final MeetingDTO meetingDTO) {
        meetingDTO.setId(meeting.getId());
        meetingDTO.setAgenda(meeting.getAgenda());
        meetingDTO.setSchduledAt(meeting.getSchduledAt());
        meetingDTO.setStartedAt(meeting.getStartedAt());
        meetingDTO.setEndedAt(meeting.getEndedAt());
        meetingDTO.setStatus(meeting.getStatus());
        meetingDTO.setActivated(meeting.getActivated());
        return meetingDTO;
    }

    private Meeting mapToEntity(final MeetingDTO meetingDTO, final Meeting meeting) {
        meeting.setAgenda(meetingDTO.getAgenda());
        meeting.setSchduledAt(meetingDTO.getSchduledAt());
        meeting.setStartedAt(meetingDTO.getStartedAt());
        meeting.setEndedAt(meetingDTO.getEndedAt());
        meeting.setStatus(meetingDTO.getStatus());
        meeting.setActivated(meetingDTO.getActivated());
        return meeting;
    }

    public Map<Long, String> getMeetingValues() {
        return meetingRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Meeting::getId, Meeting::getAgenda));
    }

}
