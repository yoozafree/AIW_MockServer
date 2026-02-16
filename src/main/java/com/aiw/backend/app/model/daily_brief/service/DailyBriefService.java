package com.aiw.backend.app.model.daily_brief.service;


import com.aiw.backend.app.model.action_item.repository.ActionItemRepository;
import com.aiw.backend.app.model.daily_brief.domain.DailyBrief;
import com.aiw.backend.app.model.daily_brief.dto.DailyBriefDTO;
import com.aiw.backend.app.model.daily_brief.repository.DailyBriefRepository;
import com.aiw.backend.app.model.meeting.repository.MeetingRepository;
import com.aiw.backend.events.BeforeDeleteMember;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@Transactional
public class DailyBriefService {

    private final DailyBriefRepository dailyBriefRepository;
    private final ActionItemRepository actionItemRepository;
    private final MeetingRepository meetingRepository;

    public DailyBriefService(final DailyBriefRepository dailyBriefRepository, ActionItemRepository actionItemRepository, MeetingRepository meetingRepository) {
        this.dailyBriefRepository = dailyBriefRepository;
        this.actionItemRepository = actionItemRepository;
        this.meetingRepository = meetingRepository;
    }

    public DailyBriefDTO getDailyBrief(Long memberId){
        // 1. 기준 시간 설정 (오늘 00:00:00 ~ 23:59:59)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 2. DB에서 저장된 AI 브리핑(요약/코멘트) 조회
        // findFirstByMemberId를 활용하되 오늘 날짜 조건이 필요할 수 있습니다.
        DailyBrief brief = dailyBriefRepository.findFirstByMemberId(memberId);

        if (brief == null) {
            throw new NotFoundException("오늘의 브리핑이 아직 생성되지 않았습니다.");
        }

        // 3. 오늘 예정된 회의 목록 조회 및 변환
        List<DailyBriefDTO.MeetingInfoDTO> meetings = meetingRepository
                .findByScheduledAtBetween(startOfDay, endOfDay).stream()
                .map(m -> DailyBriefDTO.MeetingInfoDTO.builder()
                        .meetingId(m.getId())
                        .agenda(m.getAgenda())
                        .scheduledAt(m.getScheduledAt())
                        .build())
                .toList();

        // 4. 오늘 마감인 투두(ActionItem) 목록 조회 및 변환
        List<DailyBriefDTO.TodoInfoDTO> todos = actionItemRepository
                .findByAssigneeMemberIdAndDueDateBetween(memberId, startOfDay, endOfDay).stream()
                .map(a -> DailyBriefDTO.TodoInfoDTO.builder()
                        .todoId(a.getId())
                        .title(a.getTitle())
                        .dueDate(a.getDueDate())
                        .build())
                .toList();

        // 5. 최종 DTO 조립
        return DailyBriefDTO.builder()
                .id(brief.getId())
                .date(brief.getDate())
                .summary(brief.getSummary())
                .aiComment(brief.getComment())
                .meetings(meetings)
                .todos(todos)
                .memberId(memberId)
                .activated(brief.getActivated())
                .build();
    }

    @EventListener(BeforeDeleteMember.class)
    public void on(final BeforeDeleteMember event) {
        final ReferencedException referencedException = new ReferencedException();
        final DailyBrief memberDailyBrief = dailyBriefRepository.findFirstByMemberId(event.getId());
        if (memberDailyBrief != null) {
            referencedException.setKey("member.dailyBrief.member.referenced");
            referencedException.addParam(memberDailyBrief.getId());
            throw referencedException;
        }
    }

}
