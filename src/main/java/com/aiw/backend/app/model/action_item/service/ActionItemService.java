package com.aiw.backend.app.model.action_item.service;

import com.aiw.backend.app.model.action_item.domain.ActionItem;
<<<<<<< Updated upstream
import com.aiw.backend.app.model.action_item.dto.ActionItemDTO;
import com.aiw.backend.app.model.action_item.repository.ActionItemRepository;
=======
import com.aiw.backend.app.model.action_item.model.ActionItemDTO;
import com.aiw.backend.app.model.action_item.repository.ActionItemRepository;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
>>>>>>> Stashed changes
import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ActionItemService {

<<<<<<< Updated upstream
    private final ActionItemRepository actionItemRepository;
    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

    public ActionItemService(final ActionItemRepository actionItemRepository,
            final MeetingRepository meetingRepository, final MemberRepository memberRepository) {
=======
  private final MemberRepository memberRepository;
  private final ActionItemRepository actionItemRepository;
  private final MeetingRepository meetingRepository;
  private final FileDataService fileDataService;

    public ActionItemService(
        MemberRepository memberRepository,
        ActionItemRepository actionItemRepository,
        MeetingRepository meetingRepository,
        FileDataService fileDataService) {
        this.memberRepository = memberRepository;
>>>>>>> Stashed changes
        this.actionItemRepository = actionItemRepository;
        this.meetingRepository = meetingRepository;
        this.memberRepository = memberRepository;
    }

    public List<ActionItemDTO> findAll() {
        final List<ActionItem> actionItems = actionItemRepository.findAll(Sort.by("id"));
        return actionItems.stream()
                .map(actionItem -> mapToDTO(actionItem, new ActionItemDTO()))
                .toList();
    }

    public ActionItemDTO get(final Long id) {
        return actionItemRepository.findById(id)
                .map(actionItem -> mapToDTO(actionItem, new ActionItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ActionItemDTO actionItemDTO) {
        final ActionItem actionItem = new ActionItem();
        mapToEntity(actionItemDTO, actionItem);
        return actionItemRepository.save(actionItem).getId();
    }

    public void update(final Long id, final ActionItemDTO actionItemDTO) {
        final ActionItem actionItem = actionItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(actionItemDTO, actionItem);
        actionItemRepository.save(actionItem);
    }

    public void delete(final Long id) {
        final ActionItem actionItem = actionItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        actionItemRepository.delete(actionItem);
    }

    private ActionItemDTO mapToDTO(final ActionItem actionItem, final ActionItemDTO actionItemDTO) {
        actionItemDTO.setId(actionItem.getId());
        actionItemDTO.setTitle(actionItem.getTitle());
<<<<<<< Updated upstream
=======
        actionItemDTO.setAssigneeMemberId(
            actionItem.getAssignee() == null ? null : actionItem.getAssignee().getId()
        );
>>>>>>> Stashed changes
        actionItemDTO.setDueDate(actionItem.getDueDate());
        actionItemDTO.setCompleted(actionItem.getCompleted());
        actionItemDTO.setMemo(actionItem.getMemo());
        actionItemDTO.setImage(actionItem.getImage());
        actionItemDTO.setPhase(actionItem.getPhase());
        actionItemDTO.setScope(actionItem.getScope());
        actionItemDTO.setActivated(actionItem.getActivated());
        actionItemDTO.setMeeting(actionItem.getMeeting() == null ? null : actionItem.getMeeting().getId());
        actionItemDTO.setAssigneeMember(actionItem.getAssigneeMember() == null ? null : actionItem.getAssigneeMember().getId());
        return actionItemDTO;
    }

    private ActionItem mapToEntity(final ActionItemDTO actionItemDTO, final ActionItem actionItem) {
        actionItem.setTitle(actionItemDTO.getTitle());
<<<<<<< Updated upstream
=======
        final Member assignee = memberRepository
            .findById(actionItemDTO.getAssigneeMemberId())
            .orElseThrow(() -> new NotFoundException("assignee member not found"));

        actionItem.setAssignee(assignee);

>>>>>>> Stashed changes
        actionItem.setDueDate(actionItemDTO.getDueDate());
        actionItem.setCompleted(actionItemDTO.getCompleted());
        actionItem.setMemo(actionItemDTO.getMemo());
        actionItem.setImage(actionItemDTO.getImage());
        actionItem.setPhase(actionItemDTO.getPhase());
        actionItem.setScope(actionItemDTO.getScope());
<<<<<<< Updated upstream
        actionItem.setActivated(actionItemDTO.getActivated());
        final Meeting meeting = actionItemDTO.getMeeting() == null ? null : meetingRepository.findById(actionItemDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        actionItem.setMeeting(meeting);
        final Member assigneeMember = actionItemDTO.getAssigneeMember() == null ? null : memberRepository.findById(actionItemDTO.getAssigneeMember())
                .orElseThrow(() -> new NotFoundException("assigneeMember not found"));
        actionItem.setAssigneeMember(assigneeMember);
        return actionItem;
    }

=======

        final Meeting meeting = actionItemDTO.getMeeting() == null ? null : meetingRepository.findById(actionItemDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));

        actionItem.setMeeting(meeting);

        return actionItem;
    }

>>>>>>> Stashed changes
    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
      final ActionItem meetingActionItem = actionItemRepository.findFirstByMeeting_Id(event.getId());
      if (meetingActionItem != null) {
            referencedException.setKey("meeting.actionItem.meeting.referenced");
            referencedException.addParam(meetingActionItem.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final ActionItem assigneeMemberActionItem = actionItemRepository.findFirstByAssigneeMemberId(event.getId());
        if (assigneeMemberActionItem != null) {
            referencedException.setKey("member.actionItem.assigneeMember.referenced");
            referencedException.addParam(assigneeMemberActionItem.getId());
            throw referencedException;
        }
    }

}
