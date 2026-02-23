package com.example.blog.dto.request.media;

import java.util.List;

import com.example.blog.service.MediaService.MultipartUploaded;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class MultiUploadCompleteRequest {
    private String path;
    private String uploadId;
    private List<MultipartUploaded> parts;
}