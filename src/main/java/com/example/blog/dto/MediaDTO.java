package com.example.blog.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MediaDTO {
    public enum MediaStatus{
        INIT,
        UPLOADED,
        COMPLETED,
        FAILED
    }

    MediaTypeEnum mediaType;
    String path;
    
    @Builder.Default
    MediaStatus status = MediaStatus.INIT;

    Long userId;
    String uploadId;
    Long fileSize;
    Map<String,Object> attributes;
}
