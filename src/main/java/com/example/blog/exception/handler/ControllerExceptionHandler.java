package com.example.blog.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.blog.dto.response.CommonResponse;
import com.example.blog.exception.UnauthorizedException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {
        CommonResponse<?> response = new CommonResponse<Object>(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleInvalidArgumentException(IllegalArgumentException e) {
        CommonResponse<?> response = new CommonResponse<Object>(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", e.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleServerException(RuntimeException e) {
        CommonResponse<?> response = new CommonResponse<Object>(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        CommonResponse<?> response = new CommonResponse<Object>(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
