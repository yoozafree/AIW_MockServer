package com.aiw.backend.meeting_participant.service;

import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.meeting.domain.Meeting;
import com.aiw.backend.meeting.repos.MeetingRepository;
import com.aiw.backend.meeting_participant.domain.MeetingParticipant;
import com.aiw.backend.meeting_participant.model.MeetingParticipantDTO;
import com.aiw.backend.meeting_participant.repos.MeetingParticipantRepository;
import com.aiw.backend.member.domain.Member;
import com.aiw.backend.member.repos.MemberRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MeetingParticipantService {

    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;

    public MeetingParticipantService(
            final MeetingParticipantRepository meetingParticipantRepository,
            final MemberRepository memberRepository, final MeetingRepository meetingRepository) {
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
    }

    public List<MeetingParticipantDTO> findAll() {
        final List<MeetingParticipant> meetingParticipants = meetingParticipantRepository.findAll(Sort.by("id"));
        return meetingParticipants.stream()
                .map(meetingParticipant -> mapToDTO(meetingParticipant, new MeetingParticipantDTO()))
                .toList();
    }

    public MeetingParticipantDTO get(final Long id) {
        return meetingParticipantRepository.findById(id)
                .map(meetingParticipant -> mapToDTO(meetingParticipant, new MeetingParticipantDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MeetingParticipantDTO meetingParticipantDTO) {
        final MeetingParticipant meetingParticipant = new MeetingParticipant();
        mapToEntity(meetingParticipantDTO, meetingParticipant);
        return meetingParticipantRepository.save(meetingParticipant).getId();
    }

    public void update(final Long id, final MeetingParticipantDTO meetingParticipantDTO) {
        final MeetingParticipant meetingParticipant = meetingParticipantRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(meetingParticipantDTO, meetingParticipant);
        meetingParticipantRepository.save(meetingParticipant);
    }

    public void delete(final Long id) {
        final MeetingParticipant meetingParticipant = meetingParticipantRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        meetingParticipantRepository.delete(meetingParticipant);
    }

    private MeetingParticipantDTO mapToDTO(final MeetingParticipant meetingParticipant,
            final MeetingParticipantDTO meetingParticipantDTO) {
        meetingParticipantDTO.setId(meetingParticipant.getId());
        meetingParticipantDTO.setMember(meetingParticipant.getMember() == null ? null : meetingParticipant.getMember().getId());
        meetingParticipantDTO.setMeeting(meetingParticipant.getMeeting() == null ? null : meetingParticipant.getMeeting().getId());
        return meetingParticipantDTO;
    }

    private MeetingParticipant mapToEntity(final MeetingParticipantDTO meetingParticipantDTO,
            final MeetingParticipant meetingParticipant) {
        final Member member = meetingParticipantDTO.getMember() == null ? null : memberRepository.findById(meetingParticipantDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        meetingParticipant.setMember(member);
        final Meeting meeting = meetingParticipantDTO.getMeeting() == null ? null : meetingRepository.findById(meetingParticipantDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        meetingParticipant.setMeeting(meeting);
        return meetingParticipant;
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final MeetingParticipant memberMeetingParticipant = meetingParticipantRepository.findFirstByMemberId(event.getId());
        if (memberMeetingParticipant != null) {
            referencedException.setKey("member.meetingParticipant.member.referenced");
            referencedException.addParam(memberMeetingParticipant.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
        final MeetingParticipant meetingMeetingParticipant = meetingParticipantRepository.findFirstByMeetingId(event.getId());
        if (meetingMeetingParticipant != null) {
            referencedException.setKey("meeting.meetingParticipant.meeting.referenced");
            referencedException.addParam(meetingMeetingParticipant.getId());
            throw referencedException;
        }
    }

}
