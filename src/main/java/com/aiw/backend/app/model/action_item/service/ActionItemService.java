package com.aiw.backend.app.model.action_item.service;

import com.aiw.backend.app.model.action_item.domain.ActionItem;
import com.aiw.backend.app.model.action_item.dto.ActionItemDTO;
import com.aiw.backend.app.model.action_item.repository.ActionItemRepository;
import com.aiw.backend.app.model.meeting.repository.MeetingRepository;
import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.member.domain.Member;
import com.aiw.backend.app.model.member.repository.MemberRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;
    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

  public ActionItemService(final ActionItemRepository actionItemRepository,
      final MeetingRepository meetingRepository, final MemberRepository memberRepository) {
    this.actionItemRepository = actionItemRepository;
    this.meetingRepository = meetingRepository;
    this.memberRepository = memberRepository;
  }

  // ---------------------------
  // mock 저장소 (단건조회 / 생성 / 삭제 용도)
  // ---------------------------
  private final Map<Long, ActionItemDTO> mockStorage = new LinkedHashMap<>();
  private long sequence = 1L;

  @PostConstruct
  public void initMockStorage() {
    if (!mockStorage.isEmpty()) {
      return;
    }

    ActionItemDTO item1 = new ActionItemDTO();
    item1.setId(nextId());
    item1.setTitle("액션아이템 단건 조회 테스트");
    item1.setDueDate(LocalDateTime.of(2026, 4, 12, 14, 0));
    item1.setCompleted(false);
    item1.setMemo("mock 단건 조회용 데이터");
    item1.setImage("test1.png");
    item1.setPhase(1L);
    item1.setScope("BACKEND");
    item1.setActivated(true);
    item1.setMeeting(1L);
    item1.setAssigneeMember(1L);

    ActionItemDTO item2 = new ActionItemDTO();
    item2.setId(nextId());
    item2.setTitle("액션아이템 생성/삭제 테스트");
    item2.setDueDate(LocalDateTime.of(2026, 4, 13, 16, 0));
    item2.setCompleted(false);
    item2.setMemo("mock CRUD 테스트용 데이터");
    item2.setImage("test2.png");
    item2.setPhase(2L);
    item2.setScope("FRONTEND");
    item2.setActivated(true);
    item2.setMeeting(2L);
    item2.setAssigneeMember(2L);

    mockStorage.put(item1.getId(), item1);
    mockStorage.put(item2.getId(), item2);
  }

  private long nextId() {
    return sequence++;
  }

  private ActionItemDTO copyDto(final ActionItemDTO source) {
    final ActionItemDTO copied = new ActionItemDTO();
    copied.setId(source.getId());
    copied.setTitle(source.getTitle());
    copied.setDueDate(source.getDueDate());
    copied.setCompleted(source.getCompleted());
    copied.setMemo(source.getMemo());
    copied.setImage(source.getImage());
    copied.setPhase(source.getPhase());
    copied.setScope(source.getScope());
    copied.setActivated(source.getActivated());
    copied.setMeeting(source.getMeeting());
    copied.setAssigneeMember(source.getAssigneeMember());
    return copied;
  }

  // 민지님 부분
  public List<ActionItemDTO> getActionItems(final Long assigneeMemberId) {
    final List<ActionItem> actionItems;

    if (assigneeMemberId != null) {
      actionItems = actionItemRepository.findByAssigneeMemberId(assigneeMemberId);
    } else {
      actionItems = actionItemRepository.findAll(Sort.by("id"));
    }

    return actionItems.stream()
        .map(actionItem -> mapToDTO(actionItem, new ActionItemDTO()))
        .toList();
  }

  // 단건 조회
  public ActionItemDTO get(final Long id) {
    final ActionItemDTO item = mockStorage.get(id);
    if (item == null) {
      throw new NotFoundException("mock actionItem not found. id=" + id);
    }
    return copyDto(item);
  }

  // ---------------------------
  // mock 생성
  // ---------------------------
  public Long create(final ActionItemDTO actionItemDTO) {
    final ActionItemDTO newItem = new ActionItemDTO();
    newItem.setId(nextId());
    newItem.setTitle(actionItemDTO.getTitle());
    newItem.setDueDate(actionItemDTO.getDueDate());
    newItem.setCompleted(actionItemDTO.getCompleted());
    newItem.setMemo(actionItemDTO.getMemo());
    newItem.setImage(actionItemDTO.getImage());
    newItem.setPhase(actionItemDTO.getPhase());
    newItem.setScope(actionItemDTO.getScope());
    newItem.setActivated(actionItemDTO.getActivated());
    newItem.setMeeting(actionItemDTO.getMeeting());
    newItem.setAssigneeMember(actionItemDTO.getAssigneeMember());

    mockStorage.put(newItem.getId(), newItem);
    return newItem.getId();
  }


  // 민지님 부분
  @Transactional
    public ActionItemDTO update(final Long id, final ActionItemDTO actionItemDTO) {
        final ActionItem actionItem = actionItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        // 1. DTO에 담긴 정보로 엔티티 업데이트
        actionItem.setTitle(actionItemDTO.getTitle());
        actionItem.setCompleted(actionItemDTO.getCompleted());
        actionItem.setMemo(actionItemDTO.getMemo());
        actionItem.setPhase(actionItemDTO.getPhase()); // 기존 코드에 있던 필수 필드들도 매핑 유지
        actionItem.setScope(actionItemDTO.getScope());
        actionItem.setImage(actionItemDTO.getImage());

        // 2. 담당자 변경
        if (actionItemDTO.getAssigneeMember() != null) {
            final Member newAssignee = memberRepository.findById(actionItemDTO.getAssigneeMember())
                    .orElseThrow(() -> new NotFoundException("member not found"));
            actionItem.setAssigneeMember(newAssignee);
        }

        // 3. 저장 및 반환
        ActionItem updatedItem = actionItemRepository.save(actionItem);
        return mapToDTO(updatedItem, new ActionItemDTO());
    }

  // ---------------------------
  // mock 삭제
  // ---------------------------
  public void delete(final Long id) {
    final ActionItemDTO removedItem = mockStorage.remove(id);
    if (removedItem == null) {
      throw new NotFoundException("mock actionItem not found. id=" + id);
    }
  }

    private ActionItemDTO mapToDTO(final ActionItem actionItem, final ActionItemDTO actionItemDTO) {
        actionItemDTO.setId(actionItem.getId());
        actionItemDTO.setTitle(actionItem.getTitle());
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
        actionItem.setDueDate(actionItemDTO.getDueDate());
        actionItem.setCompleted(actionItemDTO.getCompleted());
        actionItem.setMemo(actionItemDTO.getMemo());
        actionItem.setImage(actionItemDTO.getImage());
        actionItem.setPhase(actionItemDTO.getPhase());
        actionItem.setScope(actionItemDTO.getScope());
        actionItem.setActivated(actionItemDTO.getActivated());
        final Meeting meeting = actionItemDTO.getMeeting() == null ? null : meetingRepository.findById(actionItemDTO.getMeeting())
                .orElseThrow(() -> new NotFoundException("meeting not found"));
        actionItem.setMeeting(meeting);
        final Member assigneeMember = actionItemDTO.getAssigneeMember() == null ? null : memberRepository.findById(actionItemDTO.getAssigneeMember())
                .orElseThrow(() -> new NotFoundException("assigneeMember not found"));
        actionItem.setAssigneeMember(assigneeMember);
        return actionItem;
    }
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
