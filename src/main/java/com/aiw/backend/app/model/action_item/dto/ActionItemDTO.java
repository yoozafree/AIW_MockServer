package com.aiw.backend.app.model.action_item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionItemDTO {

  private Long id;

  @NotNull
  @Size(max = 255)
  private String title;

  @NotNull
  private LocalDateTime dueDate;

  @NotNull
  private Boolean completed;

  @NotNull
  @Size(max = 255)
  private String memo;

  @NotNull
  @Size(max = 255)
  private String image;

  @NotNull
  private Long phase;

  @NotNull
  @Size(max = 255)
  private String scope;

  @NotNull
  private Boolean activated;

  @NotNull
  private Long meeting;

  private Long assigneeMember;

}
