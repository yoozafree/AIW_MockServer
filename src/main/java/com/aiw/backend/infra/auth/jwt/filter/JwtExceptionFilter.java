package com.aiw.backend.infra.auth.jwt.filter;

import com.aiw.backend.infra.error.exceptions.AuthApiException;
import com.aiw.backend.infra.error.exceptions.CommonException;
import com.aiw.backend.infra.response.ResponseCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

  private final HandlerExceptionResolver handlerExceptionResolver;

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

  public JwtExceptionFilter(
      @Qualifier("handlerExceptionResolver")
      HandlerExceptionResolver handlerExceptionResolver) {
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    System.out.println("JwtExceptionFilter path=" + request.getRequestURI());

    try {
      filterChain.doFilter(request, response);
    } catch (CommonException ex) {
      throwAuthEx(request, response, ex.code());
    } catch (JwtException ex) {
      throwAuthEx(request, response, ResponseCode.UNAUTHORIZED);
    }
  }

  private void throwAuthEx(HttpServletRequest request, HttpServletResponse response, ResponseCode code) {
    handlerExceptionResolver.resolveException(request, response, null, new AuthApiException(code));
  }
}
