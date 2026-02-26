package com.aiw.backend.app.model.comment.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private Long meetingId;

    // 피드백용 회의 요약 (MeetingSummary 테이블 데이터)
    private String meetingSummary;

    // AI 피드백 요약 (Comment 테이블 - FEEDBACK_SUM)
    private CommentDTO feedbackSummary;

    // 상세 AI 피드백 (Comment 테이블 - FEEDBACK)
    private CommentDTO feedbackDetail;
}
