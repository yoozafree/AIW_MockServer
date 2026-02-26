package com.aiw.backend.app.model.comment.dto;

import lombok.*;

import java.time.OffsetDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CommentDTO {
    private Long id;

    // 코멘트 본문
    private String content;

    // "DAILY_COMMENT", "FEEDBACK_SUM", "FEEDBACK"
    private String refType;

    // 대상 ID (MemberId 또는 MeetingId 등)
    private Long refId;

    private Long memberId;

    private Boolean activated;

    private OffsetDateTime dateCreated;
    private OffsetDateTime lastUpdated;
}
