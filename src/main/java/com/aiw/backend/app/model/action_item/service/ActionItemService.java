package com.aiw.backend.app.model.action_item.service;

import com.aiw.backend.app.model.action_item.domain.ActionItem;
import com.aiw.backend.app.model.action_item.model.ActionItemDTO;
import com.aiw.backend.app.model.action_item.repos.ActionItemRepository;
import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.app.model.file.service.FileDataService;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.repos.MeetingRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;
    private final MeetingRepository meetingRepository;
    private final FileDataService fileDataService;

    public ActionItemService(final ActionItemRepository actionItemRepository,
            final MeetingRepository meetingRepository, final FileDataService fileDataService) {
        this.actionItemRepository = actionItemRepository;
        this.meetingRepository = meetingRepository;
        this.fileDataService = fileDataService;
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
        fileDataService.persistUpload(actionItem.getImage());
        return actionItemRepository.save(actionItem).getId();
    }

    public void update(final Long id, final ActionItemDTO actionItemDTO) {
        final ActionItem actionItem = actionItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        fileDataService.handleUpdate(actionItem.getImage(), actionItemDTO.getImage());
        mapToEntity(actionItemDTO, actionItem);
        actionItemRepository.save(actionItem);
    }

    public void delete(final Long id) {
        final ActionItem actionItem = actionItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        fileDataService.removeFileContent(actionItem.getImage());
        actionItemRepository.delete(actionItem);
    }

    private ActionItemDTO mapToDTO(final ActionItem actionItem, final ActionItemDTO actionItemDTO) {
        actionItemDTO.setId(actionItem.getId());
        actionItemDTO.setTitle(actionItem.getTitle());
        actionItemDTO.setAssigneeMemberId(actionItem.getAssigneeMemberId());
        actionItemDTO.setDueDate(actionItem.getDueDate());
        actionItemDTO.setCompleted(actionItem.getCompleted());
        actionItemDTO.setMemo(actionItem.getMemo());
        actionItemDTO.setImage(actionItem.getImage());
        actionItemDTO.setPhase(actionItem.getPhase());
        actionItemDTO.setScope(actionItem.getScope());
        actionItemDTO.setMeeting(actionItem.getMeeting() == null ? null : actionItem.getMeeting().getId());
        return actionItemDTO;
    }

    private ActionItem mapToEntity(final ActionItemDTO actionItemDTO, final ActionItem actionItem) {
        actionItem.setTitle(actionItemDTO.getTitle());
        actionItem.setAssigneeMemberId(actionItemDTO.getAssigneeMemberId());
        actionItem.setDueDate(actionItemDTO.getDueDate());
        actionItem.setCompleted(actionItemDTO.getCompleted());
        actionItem.setMemo(actionItemDTO.getMemo());
        actionItem.setImage(actionItemDTO.getImage());
        actionItem.setPhase(actionItemDTO.getPhase());
        actionItem.setScope(actionItemDTO.getScope());
        final Meeting meeting = actionItemDTO.getMeeting() == null ? null : meetingRepository.findById(actionItemDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        actionItem.setMeeting(meeting);
        return actionItem;
    }

    public boolean assigneeMemberIdExists(final String assigneeMemberId) {
        return actionItemRepository.existsByAssigneeMemberIdIgnoreCase(assigneeMemberId);
    }

    @EventListener(BeforeDeleteMeeting.class)
    public void on(final BeforeDeleteMeeting event) {
        final ReferencedException referencedException = new ReferencedException();
        final ActionItem meetingActionItem = actionItemRepository.findFirstByMeetingId(event.getId());
        if (meetingActionItem != null) {
            referencedException.setKey("meeting.actionItem.meeting.referenced");
            referencedException.addParam(meetingActionItem.getId());
            throw referencedException;
        }
    }

}
