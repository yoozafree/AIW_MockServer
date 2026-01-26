package com.aiw.backend.app.model.meeting_file.service;

import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.app.model.meeting_file.domain.MeetingFile;
import com.aiw.backend.app.model.meeting_file.model.MeetingFileDTO;
import com.aiw.backend.app.model.meeting_file.repository.MeetingFileRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MeetingFileService {

    private final MeetingFileRepository meetingFileRepository;
    private final MeetingRepository meetingRepository;

    public MeetingFileService(final MeetingFileRepository meetingFileRepository,
            final MeetingRepository meetingRepository) {
        this.meetingFileRepository = meetingFileRepository;
        this.meetingRepository = meetingRepository;
    }

    public List<MeetingFileDTO> findAll() {
        final List<MeetingFile> meetingFiles = meetingFileRepository.findAll(Sort.by("id"));
        return meetingFiles.stream()
                .map(meetingFile -> mapToDTO(meetingFile, new MeetingFileDTO()))
                .toList();
    }

    public MeetingFileDTO get(final Long id) {
        return meetingFileRepository.findById(id)
                .map(meetingFile -> mapToDTO(meetingFile, new MeetingFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MeetingFileDTO meetingFileDTO) {
        final MeetingFile meetingFile = new MeetingFile();
        mapToEntity(meetingFileDTO, meetingFile);
        return meetingFileRepository.save(meetingFile).getId();
    }

    public void update(final Long id, final MeetingFileDTO meetingFileDTO) {
        final MeetingFile meetingFile = meetingFileRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(meetingFileDTO, meetingFile);
        meetingFileRepository.save(meetingFile);
    }

    public void delete(final Long id) {
        final MeetingFile meetingFile = meetingFileRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        meetingFileRepository.delete(meetingFile);
    }

    private MeetingFileDTO mapToDTO(final MeetingFile meetingFile,
            final MeetingFileDTO meetingFileDTO) {
        meetingFileDTO.setId(meetingFile.getId());
        meetingFileDTO.setFileType(meetingFile.getFileType());
        meetingFileDTO.setOriginalFilename(meetingFile.getOriginalFilename());
        meetingFileDTO.setStorageUrl(meetingFile.getStorageUrl());
        meetingFileDTO.setDurationSec(meetingFile.getDurationSec());
        meetingFileDTO.setUploadedAt(meetingFile.getUploadedAt());
        meetingFileDTO.setRecordedAt(meetingFile.getRecordedAt());
        meetingFileDTO.setMeeting(meetingFile.getMeeting() == null ? null : meetingFile.getMeeting().getId());
        return meetingFileDTO;
    }

    private MeetingFile mapToEntity(final MeetingFileDTO meetingFileDTO,
            final MeetingFile meetingFile) {
        meetingFile.setFileType(meetingFileDTO.getFileType());
        meetingFile.setOriginalFilename(meetingFileDTO.getOriginalFilename());
        meetingFile.setStorageUrl(meetingFileDTO.getStorageUrl());
        meetingFile.setDurationSec(meetingFileDTO.getDurationSec());
        meetingFile.setUploadedAt(meetingFileDTO.getUploadedAt());
        meetingFile.setRecordedAt(meetingFileDTO.getRecordedAt());
        final Meeting meeting = meetingFileDTO.getMeeting() == null ? null : meetingRepository.findById(meetingFileDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        meetingFile.setMeeting(meeting);
        return meetingFile;
    }

    public boolean originalFilenameExists(final String originalFilename) {
        return meetingFileRepository.existsByOriginalFilenameIgnoreCase(originalFilename);
    }

    public boolean storageUrlExists(final String storageUrl) {
        return meetingFileRepository.existsByStorageUrlIgnoreCase(storageUrl);
    }

    public boolean meetingExists(final Long id) {
        return meetingFileRepository.existsByMeetingId(id);
    }

    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
        final MeetingFile meetingMeetingFile = meetingFileRepository.findFirstByMeetingId(event.getId());
        if (meetingMeetingFile != null) {
            referencedException.setKey("meeting.meetingFile.meeting.referenced");
            referencedException.addParam(meetingMeetingFile.getId());
            throw referencedException;
        }
    }

}
