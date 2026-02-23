package com.example.blog.service;

import com.example.blog.config.MinioProperties;
import com.example.blog.dto.MediaDTO;
import com.example.blog.dto.MediaTypeEnum;
import com.example.blog.dto.PresignedUrl;
import com.example.blog.dto.PresignedUrl.PresignedUrlPart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private static final long MULTIPART_THRESHOLD = 8 * 1024 * 1024; // 8MB = 8,388,608

    //private final MediaRepository mediaRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final MinioProperties minioProperties;

    public PresignedUrl initMedia(MediaTypeEnum mediaType, Long fileSize, Long userId) {
        return initMedia(mediaType, fileSize, userId, "posts");
    }

    public PresignedUrl initMedia(MediaTypeEnum mediaType, Long fileSize, Long userId, String subPath) {
        String filename = UUID.randomUUID() + mediaType.fileExtension();
        String path = String.format("users/%s/%s/%s", userId, subPath, filename);

        if (fileSize != null && fileSize > MULTIPART_THRESHOLD) {
            return initMultipartUpload(path, mediaType, fileSize);
        }

        return initSingleUpload(path, mediaType, fileSize);
    }

    // Generate presigned url for accessing an object in a private bucket
    public String getPresignedUrl(String path) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(minioProperties.bucket())
                .key(path)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(minioProperties.presignedUrlExpirationSeconds()))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    // Generate a presigned URL to upload an object to a private bucket
    private PresignedUrl initSingleUpload(String path, MediaTypeEnum mediaType, Long fileSize) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(minioProperties.bucket())
                .key(path)
                .contentType(mediaType.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(minioProperties.presignedUrlExpirationSeconds()))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        MediaDTO media = MediaDTO.builder().mediaType(mediaType).path(path).fileSize(fileSize).build();

        return PresignedUrl.forSingleUpload(media, presignedUrl);
    }

    // When the upload size exceeds MULTIPART_THRESHOLD, generate a presigned URL using this method instead of initSingleUpload()
    private PresignedUrl initMultipartUpload(String path, MediaTypeEnum mediaType, long fileSize) {
        String contentType = mediaType.contentType();
        
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(minioProperties.bucket())
                .key(path)
                .contentType(contentType)
                .build();

        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();

        int numberOfParts = (int) Math.ceil((double) fileSize / MULTIPART_THRESHOLD);

        List<PresignedUrlPart> presignedUrlParts = new ArrayList<>();
        for (int partNumber = 1; partNumber <= numberOfParts; partNumber++) {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(minioProperties.bucket())
                    .key(path)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();

            UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(minioProperties.presignedUrlExpirationSeconds()))
                    .uploadPartRequest(uploadPartRequest)
                    .build();

            PresignedUploadPartRequest presignedRequest = s3Presigner.presignUploadPart(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            presignedUrlParts.add(new PresignedUrl.PresignedUrlPart(partNumber, presignedUrl));
        }

        MultipartUploadInfo uploadInfo = new MultipartUploadInfo(uploadId, presignedUrlParts);

        MediaDTO media = MediaDTO.builder().mediaType(mediaType).path(path).fileSize(fileSize).uploadId(uploadInfo.uploadId()).build();

        return PresignedUrl.forMultipartUpload(media, uploadInfo.uploadId(), uploadInfo.presignedUrlParts());
    }

    // Complete multipart upload by merging all uploaded parts into a single object
    public void completeMultipartUpload(String path, String uploadId, List<MultipartUploaded> parts) {
        List<CompletedPart> s3Parts = parts.stream()
                .map(part -> CompletedPart.builder()
                        .partNumber(part.partNumber())
                        .eTag(part.eTag())
                        .build())
                .toList();

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(s3Parts)
                .build();

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(minioProperties.bucket())
                .key(path)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload)
                .build();

        s3Client.completeMultipartUpload(completeRequest);
    }

    public static record MultipartUploaded(
        int partNumber,
        String eTag
    ) {
    }

    public static record MultipartUploadInfo(
        String uploadId,
        List<PresignedUrlPart> presignedUrlParts
    ) {
    }
}