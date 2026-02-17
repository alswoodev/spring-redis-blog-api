package com.example.blog.exception.handler;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.blog.dto.response.CommonResponse;
import com.example.blog.exception.InvalidParameterException;
import com.example.blog.exception.UnauthorizedException;

@ControllerAdvice
@Component
public class ControllerExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {
        CommonResponse<?> response = new CommonResponse<Object>(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({InvalidParameterException.class})
    public ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException e) {
        String message = messageSource.getMessage(e.getCode().getMessageKey(), null, Locale.getDefault());
        CommonResponse<?> response = new CommonResponse<Object>(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", message, null);
        return ResponseEntity.badRequest().body(response);
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
