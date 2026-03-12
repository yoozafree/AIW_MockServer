package com.aiw.backend.infra.error;

import com.aiw.backend.infra.error.exceptions.AuthApiException;
import com.aiw.backend.infra.response.ApiResponse;
import com.aiw.backend.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class AuthExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(AuthApiException.class)
    public ResponseEntity<ApiResponse<String>> authApiExHandler(
        AuthApiException ex) {
      log.error("AuthApiException: code={}, message={}", ex.code(), ex.getMessage(), ex);
      return ResponseEntity
                   .status(ex.code().status())
                   .body(ApiResponse.error(ex.code()));
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> authExHandler(
        AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                   .status(HttpStatus.UNAUTHORIZED)
                   .body(ApiResponse.error(ResponseCode.UNAUTHORIZED));
    }
}
