package com.example.blog.code;

public enum UserCode implements ErrorCode{
    USER_NOT_FOUND("user.not.found"),
    INCORRECT_PASSWORD("incorrect.password"),
    USER_ALREADY_EXISTS( "user.already.exists"),
    SHORT_USER_ID( "short.user.id"),
    SHORT_PASSWORD( "short.password"),
    LONG_USER_ID("long.user.id"),
    LONG_PASSWORD("long.password"),
    LONG_NICKNAME("long.nickname"),
    INVALID_USER_ID_FORMAT("invalid.user.id.format"),
    INVALID_PASSWORD_FORMAT("invalid.password.format"),
    INVALID_NICKNAME_FORMAT("invalid.nickname.format");

    private final String messageKey;

    UserCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}