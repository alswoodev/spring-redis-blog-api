package com.example.blog.exception;

import com.example.blog.code.ErrorCode;

import lombok.Getter;

@Getter
public class InvalidParameterException extends RuntimeException {
    private final String field;
    private final ErrorCode code;
    
    public InvalidParameterException(String field, ErrorCode code) {
        super(code.getMessageKey());
        this.field = field;
        this.code = code;
    }
}
