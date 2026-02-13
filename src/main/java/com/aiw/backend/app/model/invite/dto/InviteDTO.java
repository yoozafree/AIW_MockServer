package com.aiw.backend.app.model.invite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InviteDTO {

  private Long id;

  @NotNull
  @Size(max = 255)
  private String inviteToken;

  @NotNull
  private LocalDateTime expiresAt;

  private LocalDateTime revokedAt;

  @NotNull
  private Boolean activated;

  @NotNull
  private Long team;

}
