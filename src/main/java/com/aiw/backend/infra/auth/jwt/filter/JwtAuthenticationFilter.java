package com.aiw.backend.infra.auth.jwt.filter;

import com.aiw.backend.app.model.auth.code.AuthToken;
import com.aiw.backend.app.model.auth.token.RefreshTokenService;
import com.aiw.backend.app.model.auth.token.UserBlackListRepository;
import com.aiw.backend.app.model.auth.token.entity.RefreshToken;
import com.aiw.backend.app.model.auth.token.entity.UserBlackList;
import com.aiw.backend.infra.auth.jwt.JwtTokenProvider;
import com.aiw.backend.infra.auth.jwt.TokenCookieFactory;
import com.aiw.backend.infra.auth.jwt.dto.AccessTokenDto;
import com.aiw.backend.infra.error.exceptions.CommonException;
import com.aiw.backend.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final RefreshTokenService refreshTokenService;
  private final UserBlackListRepository userBlackListRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    List<String> excludePath = new ArrayList<>();
    excludePath.addAll(
        List.of("/auth/signup", "/auth/login", "/favicon.ico", "/img", "/js", "/css", "/download"));
    excludePath.addAll(List.of("/error", "/api/member/exists", "/member/signin", "/member/signup"));
    excludePath.addAll(List.of("/swagger-ui", "/v3/api-docs"));

    excludePath.add("/api/v1/auth");

    String path = request.getRequestURI();
    boolean skip = excludePath.stream().anyMatch(path::startsWith);

    log.info("JwtFilter path={} skip={} exclude={}", path, skip, excludePath); // ✅ 로그 확인용~
    return skip;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
    if (accessToken != null) {
      try {
        if (jwtTokenProvider.validateToken(accessToken, request)) {
          Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
          if (!userBlackListRepository.existsById(authentication.getName())) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      } catch (ExpiredJwtException e) {
        // 만료면 그냥 인증 세팅 안 하고 통과 (refresh는 컨트롤러에서)
      }
    }
    filterChain.doFilter(request, response);
  }


  private void manageTokenRefresh(
      String accessToken,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    Claims claims = jwtTokenProvider.getClaims(accessToken);
    if (userBlackListRepository.existsById(claims.getSubject())) {
      return;
    }

    String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);
    RefreshToken rt = refreshTokenService.findByAccessTokenId(claims.getId());

    if (rt == null)
      return;

    if (!rt.getToken().equals(refreshToken)) {
      userBlackListRepository.save(new UserBlackList(claims.getSubject()));
      throw new CommonException(ResponseCode.SECURITY_INCIDENT);
    }

    addToken(response, claims, rt);
  }

  private void addToken(HttpServletResponse response, Claims claims, RefreshToken refreshToken) {
    String username = claims.getSubject();
    AccessTokenDto newAccessToken = jwtTokenProvider.generateAccessToken(username,
        (String) claims.get("roles"));
    Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken.getToken());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    RefreshToken newRefreshToken = refreshTokenService.renewingToken(refreshToken.getAtId(),
        newAccessToken.getJti());

    ResponseCookie accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
        newAccessToken.getToken(), jwtTokenProvider.getAccessTokenExpiration());

    ResponseCookie refreshTokenCookie = TokenCookieFactory.create(
        AuthToken.REFRESH_TOKEN.name(),
        newRefreshToken.getToken(),
        newRefreshToken.getTtl());

    response.addHeader("Set-Cookie", accessTokenCookie.toString());
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());
  }
}

