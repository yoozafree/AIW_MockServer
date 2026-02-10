package com.aiw.backend.app.model.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Boolean activated;

    @NotNull
    private Long member;

    //마이페이지 알림 기능 구현
    private Boolean meetingAlarm;
    private Boolean deadlineAlarm;
    private Boolean allAlarm;

    //수정 성공 여부 응답용 필드
    private Boolean updated;

}
