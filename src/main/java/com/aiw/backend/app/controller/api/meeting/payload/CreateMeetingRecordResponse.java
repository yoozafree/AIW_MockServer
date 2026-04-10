package com.aiw.backend.app.controller.api.meeting.payload;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMeetingRecordResponse {
    Long meetingId;
    Long fileId;
    String title;
    String status;
    LocalDateTime recordedAt;
}
