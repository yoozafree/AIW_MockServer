package com.aiw.backend.app.model.meeting_summary.service;

import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.app.model.meeting_summary.domain.MeetingSummary;
import com.aiw.backend.app.model.meeting_summary.dto.MeetingSummaryDTO;
import com.aiw.backend.app.model.meeting_summary.repository.MeetingSummaryRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MeetingSummaryService {

    private final MeetingSummaryRepository meetingSummaryRepository;
    private final MeetingRepository meetingRepository;

    public MeetingSummaryService(final MeetingSummaryRepository meetingSummaryRepository,
            final MeetingRepository meetingRepository) {
        this.meetingSummaryRepository = meetingSummaryRepository;
        this.meetingRepository = meetingRepository;
    }

    public List<MeetingSummaryDTO> findAll() {
        final List<MeetingSummary> meetingSummaries = meetingSummaryRepository.findAll(Sort.by("id"));
        return meetingSummaries.stream()
                .map(meetingSummary -> mapToDTO(meetingSummary, new MeetingSummaryDTO()))
                .toList();
    }

    public MeetingSummaryDTO get(final Long id) {
        return meetingSummaryRepository.findById(id)
                .map(meetingSummary -> mapToDTO(meetingSummary, new MeetingSummaryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MeetingSummaryDTO meetingSummaryDTO) {
        final MeetingSummary meetingSummary = new MeetingSummary();
        mapToEntity(meetingSummaryDTO, meetingSummary);
        return meetingSummaryRepository.save(meetingSummary).getId();
    }

    public void update(final Long id, final MeetingSummaryDTO meetingSummaryDTO) {
        final MeetingSummary meetingSummary = meetingSummaryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(meetingSummaryDTO, meetingSummary);
        meetingSummaryRepository.save(meetingSummary);
    }

    public void delete(final Long id) {
        final MeetingSummary meetingSummary = meetingSummaryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        meetingSummaryRepository.delete(meetingSummary);
    }

    private MeetingSummaryDTO mapToDTO(final MeetingSummary meetingSummary,
            final MeetingSummaryDTO meetingSummaryDTO) {
        meetingSummaryDTO.setId(meetingSummary.getId());
        meetingSummaryDTO.setSummaryTitle(meetingSummary.getSummaryTitle());
        meetingSummaryDTO.setSummaryText(meetingSummary.getSummaryText());
        meetingSummaryDTO.setActivated(meetingSummary.getActivated());
        meetingSummaryDTO.setKeyDecision(meetingSummary.getKeyDecision());
        meetingSummaryDTO.setMeeting(meetingSummary.getMeeting() == null ? null : meetingSummary.getMeeting().getId());
        return meetingSummaryDTO;
    }

    private MeetingSummary mapToEntity(final MeetingSummaryDTO meetingSummaryDTO,
            final MeetingSummary meetingSummary) {
        meetingSummary.setSummaryTitle(meetingSummaryDTO.getSummaryTitle());
        meetingSummary.setSummaryText(meetingSummaryDTO.getSummaryText());
        meetingSummary.setActivated(meetingSummaryDTO.getActivated());
        meetingSummary.setKeyDecision(meetingSummaryDTO.getKeyDecision());
        final Meeting meeting = meetingSummaryDTO.getMeeting() == null ? null : meetingRepository.findById(meetingSummaryDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        meetingSummary.setMeeting(meeting);
        return meetingSummary;
    }

    public boolean summaryTextExists(final String summaryText) {
        return meetingSummaryRepository.existsBySummaryText(summaryText);
    }

    public boolean keyDecisionExists(final String keyDecision) {
        return meetingSummaryRepository.existsByKeyDecisionIgnoreCase(keyDecision);
    }

    public boolean meetingExists(final Long id) {
        return meetingSummaryRepository.existsByMeetingId(id);
    }

    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
        final MeetingSummary meetingMeetingSummary = meetingSummaryRepository.findFirstByMeetingId(event.getId());
        if (meetingMeetingSummary != null) {
            referencedException.setKey("meeting.meetingSummary.meeting.referenced");
            referencedException.addParam(meetingMeetingSummary.getId());
            throw referencedException;
        }
    }

}
