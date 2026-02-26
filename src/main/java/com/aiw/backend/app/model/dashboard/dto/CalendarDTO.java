package com.aiw.backend.app.model.dashboard.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDTO {
    private Map<LocalDate, List<CalendarItemDTO>> monthlySchedules;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarItemDTO {
        private Long id;          // 원본 엔티티의 ID (상세페이지 이동용)

        private String title;     // 표시할 제목 (Meeting의 agenda 또는 ActionItem의 title)

        private LocalDateTime time; // 시작 시간 (Meeting의 scheduledAt 또는 ActionItem의 dueDate)

        private String type;      // 구분값 ("MEETING" 또는 "TODO")

    }
}
