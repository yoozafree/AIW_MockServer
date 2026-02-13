package com.aiw.backend.app.model.meeting_participant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingParticipantDTO {

    private Long id;

    @NotNull
    private Long member;

    @NotNull
    private Long meeting;

}
