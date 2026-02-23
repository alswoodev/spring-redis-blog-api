package com.example.blog.code;

public interface ErrorCode {
    public String getMessageKey();

    default String getCode() {
        return ((Enum<?>) this).name();
    }
}
