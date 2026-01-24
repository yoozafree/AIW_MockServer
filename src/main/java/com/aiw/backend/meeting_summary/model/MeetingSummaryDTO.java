package com.aiw.backend.meeting_summary.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingSummaryDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String summaryTitle;

    @NotNull
    @MeetingSummarySummaryTextUnique
    private String summaryText;

    private Boolean activated;

    @NotNull
    @Size(max = 255)
    @MeetingSummaryKeyDecisionUnique
    private String keyDecision;

    @NotNull
    @MeetingSummaryMeetingUnique
    private Long meeting;

}
