package com.TalentWorld.backend.excepiton;


import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter

public class BusinessException extends RuntimeException {
    private final String errorCode;
    //return status code
    private final HttpStatus httpStatus;
    //logging error
    private final LocalDateTime timestamp;
    private final Object details;

    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
        this.details = null;
    }

    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
        this.details = null;
    }
}