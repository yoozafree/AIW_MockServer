package com.aiw.backend.app.model.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenDto {
  private String accessToken;
  private String refreshToken;
  private String atId; // access token 식별하는 고유키, refresh 토큰 저장할 때의 key
  private String grantType;
  private Long expiresIn;
  private Long refreshExpiresIn;
}
