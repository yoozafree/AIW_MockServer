package com.aiw.backend.app.model.meeting.service;

import com.aiw.backend.app.model.meeting.repository.MeetingRepository;
import com.aiw.backend.app.model.project.domain.Project;
import com.aiw.backend.app.model.project.repository.ProjectRepository;
import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.dto.MeetingDTO;
import com.aiw.backend.util.CustomCollectors;
import com.aiw.backend.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher publisher;

    public MeetingService(final MeetingRepository meetingRepository,
                          final ProjectRepository projectRepository,
            final ApplicationEventPublisher publisher) {
        this.meetingRepository = meetingRepository;
        this.projectRepository = projectRepository;
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

    @Transactional
    public MeetingDTO create(final MeetingDTO meetingDTO) {
        final Meeting meeting = new Meeting();
        mapToEntity(meetingDTO, meeting);

        // 프로젝트 연결 로직
        Project project = projectRepository.findById(meetingDTO.getProjectId())
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
        meeting.setProject(project);

        //DB에 저장
        final Meeting savedMeeting = meetingRepository.save(meeting);

        //저장된 엔티티를 다시 DTO로 변환하여 반환 (ID가 채워진 상태)
        return mapToDTO(savedMeeting, new MeetingDTO());
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
        meetingDTO.setScheduledAt(meeting.getScheduledAt());
        meetingDTO.setStartedAt(meeting.getStartedAt());
        meetingDTO.setEndedAt(meeting.getEndedAt());
        meetingDTO.setStatus(meeting.getStatus());
        meetingDTO.setActivated(meeting.getActivated());
        // 추가: DB의 값을 DTO로 옮겨줌
        meetingDTO.setCreatedType(meeting.getCreatedType());
        return meetingDTO;
    }

    private Meeting mapToEntity(final MeetingDTO meetingDTO, final Meeting meeting) {
        meeting.setAgenda(meetingDTO.getAgenda());
        meeting.setScheduledAt(meetingDTO.getScheduledAt());
        meeting.setStartedAt(meetingDTO.getStartedAt());
        meeting.setEndedAt(meetingDTO.getEndedAt());
        meeting.setStatus(meetingDTO.getStatus());
        meeting.setActivated(meetingDTO.getActivated());
        // 추가: 클라이언트가 보낸 값을 엔티티에 세팅
        meeting.setCreatedType(meetingDTO.getCreatedType());
        return meeting;
    }

    public Map<Long, String> getMeetingValues() {
        return meetingRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Meeting::getId, Meeting::getAgenda));
    }

}
