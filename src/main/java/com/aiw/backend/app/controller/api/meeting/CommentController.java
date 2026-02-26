package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.model.comment.dto.FeedbackDTO;
import com.aiw.backend.app.model.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Comment", description = "AI 피드백 및 코멘트 조회 API")
public class CommentController {
    private final CommentService commentService;

    //피드백 요약 조회
    @GetMapping("/feedback/summary/{meetingId}")
    @Operation(summary = "피드백 요약 조회", description = "특정 회의의 AI 피드백 요약(FEEDBACK_SUM)을 조회합니다.")
    public ResponseEntity<FeedbackDTO> getFeedbackSummary(
            @PathVariable final Long meetingId,
            @RequestParam(name = "memberId") final Long memberId) {

        return ResponseEntity.ok(commentService.getFeedbackSummary(memberId, meetingId));
    }

    //상세 AI 피드백 조회
    @GetMapping("/feedback/detail/{meetingId}")
    @Operation(summary = "상세 피드백 조회", description = "특정 회의의 상세 AI 피드백(FEEDBACK)을 조회합니다.")
    public ResponseEntity<FeedbackDTO> getFeedbackDetail(
            @PathVariable final Long meetingId,
            @RequestParam(name = "memberId") final Long memberId) {

        return ResponseEntity.ok(commentService.getFeedbackDetail(memberId, meetingId));
    }

    @GetMapping("/feedback/meeting-summary/{meetingId}")
    @Operation(summary = "피드백용 회의 요약 조회", description = "피드백의 근거가 되는 회의 요약 텍스트(MeetingSummary)를 조회합니다.")
    public ResponseEntity<FeedbackDTO> getMeetingSummary(
            @PathVariable final Long meetingId) {
        // memberId가 필요 없는 이유는 MeetingSummary는 회의당 하나(공통)이기 때문입니다.
        return ResponseEntity.ok(commentService.getMeetingSummaryForFeedback(meetingId));
    }
}
