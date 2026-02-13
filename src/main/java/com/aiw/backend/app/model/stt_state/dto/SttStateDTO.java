package com.aiw.backend.app.model.stt_state.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SttStateDTO {

  private Long id;

  @NotNull
  @Size(max = 255)
  private String state;

  @NotNull
  @Size(max = 255)
  private String language;

  @NotNull
  @Size(max = 255)
  @SttStateRawJsonUrlUnique
  private String rawJsonUrl;

  @NotNull
  @Size(max = 255)
  private String startedAt;

  @NotNull
  @Size(max = 255)
  private String endedAt;

  @NotNull
  private String errorMessage;

  private Boolean activated;

  @NotNull
  @SttStateMeetingUnique
  private Long meeting;

}
