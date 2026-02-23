package com.example.blog.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class PresignedUrl{

    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class PresignedUrlPart{
        int partNumber;
        String presignedUrl;
    }

    MediaDTO media;
    String presignedUrl;
    String uploadId;
    List<PresignedUrlPart> presignedUrlParts;

    public static PresignedUrl forSingleUpload(MediaDTO media, String presignedUrl) {
        return new PresignedUrl(media, presignedUrl, null, null);
    }

    public static PresignedUrl forMultipartUpload(MediaDTO media, String uploadId, List<PresignedUrlPart> presignedUrlParts) {
        return new PresignedUrl(media, null, uploadId, presignedUrlParts);
    }
}