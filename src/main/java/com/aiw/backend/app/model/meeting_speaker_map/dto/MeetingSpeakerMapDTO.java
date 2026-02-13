package com.aiw.backend.app.model.meeting_speaker_map.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingSpeakerMapDTO {

  @Size(max = 255)
  @MeetingSpeakerMapIdValid
  private String id;

  @NotNull
  @Size(max = 20)
  private String speakerLabel;

  @NotNull
  @Size(max = 255)
  private String meetingId;

  private Boolean activated;

  @NotNull
  private Long meeting;


}
