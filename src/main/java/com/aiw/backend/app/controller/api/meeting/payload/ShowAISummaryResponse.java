package com.aiw.backend.app.controller.api.meeting.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowAISummaryResponse {
    private Long meetingId;
    private String title;
    private String startedAt;
    private List<String> participants;
    private List<String> decisionItems;
    private List<String> summarySegments;
}
