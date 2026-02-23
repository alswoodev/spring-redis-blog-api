package com.example.blog.dto.request.media;

import com.example.blog.dto.MediaTypeEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MediaInitRequest{
    private MediaTypeEnum mediaType;
    private Long fileSize;
}