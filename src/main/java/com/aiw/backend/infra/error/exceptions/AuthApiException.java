package com.aiw.backend.infra.error.exceptions;


import com.aiw.backend.infra.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthApiException extends CommonException{

    public AuthApiException(ResponseCode code) {
        super(code);
    }

    public AuthApiException(ResponseCode code, Exception e) {
        super(code, e);
        log.error(e.getMessage(), e);
    }
}
