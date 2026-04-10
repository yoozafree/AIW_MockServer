package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.controller.api.meeting.payload.ShowActionItemResponse;
import com.aiw.backend.app.controller.api.meeting.payload.CreateMeetingRecordResponse;
import com.aiw.backend.app.controller.api.meeting.payload.CreateMeetingRecordRequest;
import com.aiw.backend.app.controller.api.meeting.payload.ShowMeetingListResponse;
import com.aiw.backend.app.controller.api.meeting.payload.ShowAISummaryResponse;
import com.aiw.backend.app.controller.api.meeting.payload.ShowSttStatusResponse;
import com.aiw.backend.app.model.meeting.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/api/v1/meetings", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(final MeetingService meetingService) {
        this.meetingService = meetingService;
    }

  // 회의 리스트 조회
  @GetMapping("/record")
  @Operation(summary = "회의 리스트 조회")
  public ResponseEntity<List<ShowMeetingListResponse>> getMeetingRecords() {
    return ResponseEntity.ok(meetingService.getMeetingRecords());
  }

  // 회의 생성 (녹음)
  @PostMapping("/record")
  @Operation(summary = "회의 생성 (녹음)")
  public ResponseEntity<CreateMeetingRecordResponse> createMeeting(
      @RequestBody @Valid CreateMeetingRecordRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(meetingService.createMeeting(request));
  }

  // 회의 생성 (녹음 파일 업로드)
  @PostMapping(value = "/record-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "회의 생성 (녹음 파일 업로드)")
  public ResponseEntity<CreateMeetingRecordResponse> createMeetingByFile(
      @RequestPart("file") MultipartFile file,
      @ModelAttribute CreateMeetingRecordRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(meetingService.createMeetingByFile(file, request));
  }

  // STT 상태 조회
  @GetMapping("/{meetingId}/stt/status")
  @Operation(summary = "회의 STT 상태 조회")
  public ResponseEntity<ShowSttStatusResponse> getSttStatus(
      @PathVariable Long meetingId
  ) {
    return ResponseEntity.ok(meetingService.getSttStatus(meetingId));
  }

  // STT 원본 파일 다운로드
  @GetMapping("/{meetingId}/stt/download")
  @Operation(summary = "회의 STT 원문 파일 다운로드")
  public ResponseEntity<Resource> downloadMeetingStt(@PathVariable Long meetingId) {
    return meetingService.downloadMeetingStt(meetingId);
  }

  // AI 요약 생성
  @PostMapping("/{meetingId}/summary")
  @Operation(summary = "회의 AI 요약 생성")
  public ResponseEntity<ShowAISummaryResponse> createSummary(
      @PathVariable Long meetingId
  ) {
    return ResponseEntity.ok(meetingService.createSummary(meetingId));
  }

  // 액션아이템 조회
  @GetMapping("/{meetingId}/action-items")
  @Operation(summary = "회의 요약 기반 액션아이템 조회")
  public ResponseEntity<List<ShowActionItemResponse>> getActionItems(
      @PathVariable Long meetingId
  ) {
    return ResponseEntity.ok(meetingService.getActionItems(meetingId));
  }

}
