package com.aiw.backend.app.controller.api.mainpage;

import com.aiw.backend.app.model.dashboard.dto.CalendarDTO;
import com.aiw.backend.app.model.dashboard.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/dashboard/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Calendar", description = "대시보드 캘린더 일정 조회 API")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    @Operation(summary = "월간 캘린더 일정 조회",
            description = "특정 연도와 월의 모든 회의 및 할 일을 날짜별로 그룹화하여 조회합니다.")
    public ResponseEntity<CalendarDTO> getMonthlyCalendar(
            @RequestParam(name = "memberId") final Long memberId,
            @RequestParam(name = "year") final int year,
            @RequestParam(name = "month") final int month) {

        // 서비스에서 날짜별(Map)로 정리된 데이터를 가져옵니다.
        CalendarDTO response = calendarService.getMonthlyCalendar(memberId, year, month);

        return ResponseEntity.ok(response);
    }
}
