package com.aiw.backend.app.controller.api.meeting.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowMeetingSttResponse {
    Long meetingId;
    List<CreateSttSegmentResponse> segments;
}
