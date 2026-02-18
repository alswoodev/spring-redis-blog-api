package com.example.blog.code;

public enum PostCode implements ErrorCode {
    POST_NOT_FOUND("post.not.found"),
    POST_NO_TITLE("post.no.title"),
    POST_NO_WRITER("post.no.writer"),
    FILE_NOT_FOUND("file.not.found"),
    FILE_NO_URL("file.no.url"),
    FILE_NO_NAME("file.no.name"),
    FILE_NO_EXTENSION("file.no.extension"),
    ADDITIONAL_NO_POST_ID("additional.no.post.id"),
    TAG_NOT_FOUND("tag.not.found"),
    TAG_NO_SPACE("tag.no.space"),
    TAG_TOO_SHORT("tag.too.short"),
    TAG_TOO_LONG("tag.too.long"),
    TAG_NO_NAME("tag.no.name"),
    COMMENT_NOT_FOUND("comment.not.found"),
    COMMENT_TOO_LONG("comment.too.long"),
    COMMENT_EMPTY("comment.empty");


    private final String messageKey;
    
    PostCode(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String getMessageKey(){
        return messageKey;
    }
}
