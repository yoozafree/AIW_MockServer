package com.aiw.backend.meeting_speaker_map.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingSpeakerMapDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String speakerLabel;

    @NotNull
    private Long meeting;

}
