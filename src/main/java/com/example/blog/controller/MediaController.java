package com.example.blog.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.blog.aop.LoginCheck;
import com.example.blog.aop.LoginCheck.UserType;
import com.example.blog.dto.PresignedUrl;
import com.example.blog.dto.request.media.MediaInitRequest;
import com.example.blog.dto.request.media.MultiUploadCompleteRequest;
import com.example.blog.service.MediaService;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/media/init")
    @LoginCheck(type = UserType.USER)
    @ResponseStatus(HttpStatus.OK)
    public PresignedUrl initMedia(
            @Parameter(hidden=true) Long userId,
            @RequestBody MediaInitRequest request
    ) {
        PresignedUrl result = mediaService.initMedia(request.getMediaType(), request.getFileSize(), userId);
        return result;
    }

    @PostMapping("/media/uploaded")
    @LoginCheck( type = UserType.USER )
    public void mediaUploaded(
            @Parameter(hidden=true) Long userId,
            @RequestBody MultiUploadCompleteRequest request
    ) {
        mediaService.completeMultipartUpload(request.getPath(), request.getUploadId(), request.getParts());
    }

    @GetMapping("/media/presigned-url")
    @LoginCheck( type = UserType.USER )
    public PresignedUrlResponse getPresignedUrl(@Parameter(hidden=true) Long userId, @RequestParam String path) {
        String presignedUrl = mediaService.getPresignedUrl(path);
        return new PresignedUrlResponse(presignedUrl);
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    private static class PresignedUrlResponse{
        private String presignedUrl;
    }
}