package com.aiw.backend.app.model.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;

    @NotNull
    private String content;

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

    //대시보드 알림 기능 구현
    private String type;//"SETTING", "ANNOUNCEMENT", "FEEDBACK", "TODO", "MEETING"
    private Boolean isRead;//읽음 여부
    private String title;//제목
    private Long teamId;//팀 ID
    private Long relatedId;//관련 엔티티 ID
    private Long authorId;//작성자 ID
    private String authorName;//작성자 이름
    private OffsetDateTime dateCreated;//생성 일시
    private OffsetDateTime lastUpdated;//수정 일시

}
