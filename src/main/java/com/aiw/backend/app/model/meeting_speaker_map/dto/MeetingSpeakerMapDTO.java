package com.aiw.backend.app.model.meeting_speaker_map.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MeetingSpeakerMapDTO {

<<<<<<< Updated upstream
  @Size(max = 255)
  @MeetingSpeakerMapIdValid
  private String id;
=======
  private Long id;
>>>>>>> Stashed changes

  @NotNull
  @Size(max = 20)
  private String speakerLabel;

  @NotNull
<<<<<<< Updated upstream
  @Size(max = 255)
  private String meetingId;

=======
>>>>>>> Stashed changes
  private Boolean activated;

  @NotNull
  private Long meeting;


}
