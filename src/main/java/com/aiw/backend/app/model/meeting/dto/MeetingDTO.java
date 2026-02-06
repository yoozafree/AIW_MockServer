package com.aiw.backend.app.model.meeting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String agenda;

    @NotNull
    private LocalDateTime schduledAt;

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime endedAt;

    @NotNull
    @Size(max = 255)
    private String status;

    @NotNull
    private Boolean activated;

}
