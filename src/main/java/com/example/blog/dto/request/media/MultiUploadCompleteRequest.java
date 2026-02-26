package com.example.blog.dto.request.media;

import java.util.List;

import com.example.blog.service.MediaService.MultipartUploaded;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class MultiUploadCompleteRequest {
    @NotNull(message="{something.wrong}")
    private String path;
    @NotNull(message="{something.wrong}")
    private String uploadId;
    @NotNull(message="{something.wrong}")
    private List<MultipartUploaded> parts;
}