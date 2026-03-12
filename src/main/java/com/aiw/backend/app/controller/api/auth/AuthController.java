package com.aiw.backend.app.controller.api.auth;

import com.aiw.backend.app.controller.api.auth.payload.LoginRequest;
import com.aiw.backend.app.controller.api.auth.payload.SignupRequest;
import com.aiw.backend.app.model.auth.AuthService;
import com.aiw.backend.app.model.auth.code.AuthToken;
import com.aiw.backend.app.model.auth.dto.TokenDto;
import com.aiw.backend.app.model.auth.token.RefreshTokenService;
import com.aiw.backend.app.model.auth.token.entity.RefreshToken;
import com.aiw.backend.app.model.member.dto.MemberDTO;
import com.aiw.backend.infra.auth.jwt.JwtTokenProvider;
import com.aiw.backend.infra.auth.jwt.TokenCookieFactory;
import com.aiw.backend.infra.auth.jwt.dto.AccessTokenDto;
import com.aiw.backend.infra.error.exceptions.AuthApiException;
import com.aiw.backend.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;


  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
    authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginRequest req,
      HttpServletResponse response) {
    TokenDto tokenDto = authService.signin(req);

    // 쿠키 TTL: 초로 맞추기 (jwtTokenProvider 값이 ms라면 /1000)
    long accessTtlSeconds = jwtTokenProvider.getAccessTokenExpiration() / 1000;
    long refreshTtlSeconds = jwtTokenProvider.getRefreshTokenExpiration() / 1000;

    response.addHeader("Set-Cookie",
        TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(), tokenDto.getAccessToken(), accessTtlSeconds).toString());
    response.addHeader("Set-Cookie",
        TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(), tokenDto.getRefreshToken(), refreshTtlSeconds).toString());

    return ResponseEntity.ok(tokenDto);
  }

  // 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);

    if (accessToken != null && !accessToken.isBlank()) {
      try {
        Claims claims = jwtTokenProvider.getClaims(accessToken);
        // Redis refresh 삭제 (key=atId=jti)
        refreshTokenService.deleteByAccessTokenId(claims.getId());
      } catch (JwtException e) {
        // 토큰이 깨졌거나 서명 불일치면 그냥 무시(로그아웃은 idempotent)
      }
    }

    // 쿠키 만료
    response.addHeader("Set-Cookie",
        TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name()).toString());
    response.addHeader("Set-Cookie",
        TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name()).toString());

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenDto> refresh(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
    String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);

    if (accessToken == null || refreshToken == null) {
      throw new AuthApiException(ResponseCode.UNAUTHORIZED);
    }

    Claims claims;
    try {
      claims = jwtTokenProvider.getClaims(accessToken); // 만료여도 claims 반환됨
    } catch (JwtException e) {
      throw new AuthApiException(ResponseCode.UNAUTHORIZED);
    }

    RefreshToken stored = refreshTokenService.findByAccessTokenId(claims.getId());
    if (stored == null || !stored.getToken().equals(refreshToken)) {
      throw new AuthApiException(ResponseCode.UNAUTHORIZED);
    }

    // roles 재사용
    String roles = (String) claims.get("roles");
    AccessTokenDto newAt = jwtTokenProvider.generateAccessToken(claims.getSubject(), roles);
    RefreshToken newRt = refreshTokenService.renewingToken(claims.getId(), newAt.getJti());

    TokenDto dto = TokenDto.builder()
        .accessToken(newAt.getToken())
        .refreshToken(newRt.getToken())
        .atId(newAt.getJti())
        .grantType("Bearer")
        .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
        .refreshExpiresIn(jwtTokenProvider.getRefreshTokenExpiration())
        .build();

    long accessTtlSeconds = jwtTokenProvider.getAccessTokenExpiration() / 1000;
    long refreshTtlSeconds = jwtTokenProvider.getRefreshTokenExpiration() / 1000;

    response.addHeader("Set-Cookie",
        TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(), dto.getAccessToken(), accessTtlSeconds).toString());
    response.addHeader("Set-Cookie",
        TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(), dto.getRefreshToken(), refreshTtlSeconds).toString());

    return ResponseEntity.ok(dto);
  }
}