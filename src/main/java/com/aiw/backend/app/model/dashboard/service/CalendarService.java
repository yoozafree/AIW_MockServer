package com.aiw.backend.app.model.dashboard.service;

import com.aiw.backend.app.model.action_item.repository.ActionItemRepository;
import com.aiw.backend.app.model.dashboard.dto.CalendarDTO;
import com.aiw.backend.app.model.dashboard.dto.CalendarDTO.CalendarItemDTO;
import com.aiw.backend.app.model.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarService {
    private final MeetingRepository meetingRepository;
    private final ActionItemRepository actionItemRepository;

    public CalendarDTO getMonthlyCalendar(final Long memberId, final int year, final int month){

        // 1. 조회할 달의 시작과 끝 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // 2. 해당 월의 모든 회의(Meeting) 조회
        List<CalendarDTO.CalendarItemDTO> meetings = meetingRepository
                .findByScheduledAtBetween(startDateTime, endDateTime)
                .stream()
                .map(meeting -> CalendarDTO.CalendarItemDTO.builder()
                        .id(meeting.getId())
                        .title(meeting.getAgenda())
                        .time(meeting.getScheduledAt())
                        .type("MEETING")
                        .build())
                .toList();

        // 3. 해당 월의 모든 할 일(ActionItem) 조회
        List<CalendarDTO.CalendarItemDTO> todos = actionItemRepository
                .findByAssigneeMemberIdAndDueDateBetween(memberId, startDateTime, endDateTime)
                .stream()
                .map(todo -> CalendarDTO.CalendarItemDTO.builder()
                        .id(todo.getId())
                        .title(todo.getTitle())
                        .time(todo.getDueDate())
                        .type("TODO")
                        .build())
                .toList();

        List<CalendarItemDTO> allItems = new ArrayList<>();
        allItems.addAll(meetings);
        allItems.addAll(todos);

        // 5. 날짜(LocalDate)별로 그룹화 (UI에서 날짜 클릭 시 리스트를 바로 보여주기 위함)
        Map<LocalDate, List<CalendarItemDTO>> groupedSchedules = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getTime().toLocalDate()));

        return CalendarDTO.builder()
                .monthlySchedules(groupedSchedules)
                .build();
    }
}
