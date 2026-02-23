package com.example.blog.exception.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.blog.dto.response.ErrorResponse;
import com.example.blog.exception.InvalidParameterException;
import com.example.blog.exception.UnauthorizedException;

@ControllerAdvice
@Component
public class ControllerExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("UNAUTHORIZED", e.getMessage()));
    }

    @ExceptionHandler({InvalidParameterException.class})
    public ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException e, Locale locale) {
        String message = messageSource.getMessage(e.getCode().getMessageKey(), null, locale);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getCode().getCode(), message));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleInvalidArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        //StringWriter sw = new StringWriter();
        //PrintWriter pw = new PrintWriter(sw);
        //e.printStackTrace(pw);
        //String stackTrace = sw.toString();
        return ResponseEntity.badRequest().body(new ErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage()));
    }
}
