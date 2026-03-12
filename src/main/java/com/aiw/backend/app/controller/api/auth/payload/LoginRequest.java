package com.aiw.backend.app.controller.api.auth.payload;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
  //@Schema(example="test@example.com", description="로그인 이메일")
  private String email;
  //@Schema(example = "123qwe!@#")
  private String password;
}
