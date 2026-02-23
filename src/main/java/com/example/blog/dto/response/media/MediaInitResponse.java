package com.example.blog.dto.response.media;

import java.util.List;

import com.example.blog.dto.PresignedUrl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MediaInitResponse {
    private String uploadId;
    private String path;
    private String presignedUrl;
    private List<PresignedUrl> parts;
}