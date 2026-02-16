package com.aiw.backend.app.model.daily_brief.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBriefDTO {

    private Long id;

    private LocalDateTime date; //브리핑 기준 날짜

    private String summary; //오늘의 일정 요약

    private String aiComment; //AI 데일리 코멘트

    private List<MeetingInfoDTO> meetings;

    private List<TodoInfoDTO> todos; //오늘 마감 투두 목록

    private Long memberId;
    private Boolean activated;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeetingInfoDTO {
        private Long meetingId;
        private String agenda;
        private LocalDateTime scheduledAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodoInfoDTO {
        private Long todoId;
        private String title;
        private LocalDateTime dueDate;
    }

}
