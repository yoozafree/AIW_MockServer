package com.aiw.backend.infra.auth.jwt.filter;

import com.aiw.backend.app.model.auth.code.AuthToken;
import com.aiw.backend.app.model.auth.token.RefreshTokenService;
import com.aiw.backend.infra.auth.jwt.JwtTokenProvider;
import com.aiw.backend.infra.auth.jwt.TokenCookieFactory;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

  private final RefreshTokenService refreshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.equals("/swagger-ui.html")
        || path.equals("/favicon.ico")
        || path.startsWith("/css")
        || path.startsWith("/js")
        || path.startsWith("/img")
        || path.startsWith("/api/v1/auth");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);

    if(accessToken == null){
      filterChain.doFilter(request,response);
      return;
    }

    String path = request.getRequestURI();
    Claims claims  = jwtTokenProvider.getClaims(accessToken);

    if(path.equals("/auth/logout")){
      refreshTokenService.deleteByAccessTokenId(claims.getId());
      SecurityContextHolder.clearContext();
      ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(AuthToken.ACCESS_TOKEN.name());
      ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(AuthToken.REFRESH_TOKEN.name());
      ResponseCookie expiredSessionId = TokenCookieFactory.createExpiredToken(AuthToken.AUTH_SERVER_SESSION_ID.name());
      response.addHeader("Set-Cookie", expiredAccessToken.toString());
      response.addHeader("Set-Cookie", expiredRefreshToken.toString());
      response.addHeader("Set-Cookie", expiredSessionId.toString());
      response.sendRedirect("/");
    }

    filterChain.doFilter(request,response);
  }
}
