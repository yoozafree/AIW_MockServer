package com.aiw.backend.app.model.announcement.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {

    private Long id;
    private String content; // 공지 내용
    private Boolean activated;

    private Long teamId;    // 대상 팀
    private Long writerId;  // 작성자
    private String writerName; // 화면에 표시할 작성자 이름

    private OffsetDateTime dateCreated;
    private OffsetDateTime lastUpdated;
}
