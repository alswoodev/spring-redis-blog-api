package com.example.blog.service;

import com.example.blog.config.MinioProperties;
import com.example.blog.dto.MediaTypeEnum;
import com.example.blog.dto.PresignedUrl;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.UserDTO.Status;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MediaServiceTest {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private UserService userService;

    private UserDTO testUser;

    private Long id;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO();
        testUser.setUserId("testuser11");
        testUser.setPassword("password123");
        testUser.setNickname("테스터");
        testUser.setStatus(Status.DEFAULT);
        testUser.setCreateTime(new Date());
        testUser.setAdmin(false);

        userService.register(testUser);
        id = userService.login("testuser", "password123").getId();
        testUser.setId(id);

        // Config RestAssured to solve auto-adding charset, double encoding issue.
        RestAssured.config = RestAssuredConfig.config() // To solve Auto-adding charset problem
                .encoderConfig(EncoderConfig.encoderConfig()
                    .appendDefaultContentCharsetToContentTypeIfUndefined(false)
                );
        RestAssured.urlEncodingEnabled = false; // To solve double encoding problem
    }

    @Test
    @DisplayName("Success : Single file(image) upload")
    void uploadSingleFileSuccess() {
        // given
        Long fileSize = 1024L * 1024L; // File with a size under 8MB
        byte[] content = "fake-image-binary-content".getBytes(); // content

        // when
        PresignedUrl presignedUrl = mediaService.initMedia(MediaTypeEnum.IMAGE, fileSize, id);

        // PUT request for uploading content
        Response response = RestAssured.given()
                //.log().all()
                .header("Content-Type", "image/jpeg")
                .body(content)
                .put(presignedUrl.getPresignedUrl());

        assertThat(response.getStatusCode()).isEqualTo(200);

        // then
        assertThat(response.getStatusCode()).isEqualTo(200); // Upload succeeded without issues
        assertThat(doesObjectExist(presignedUrl.getMedia().getPath())).isTrue(); // The uploaded file actually exists in S3
    }

    @Test
    @DisplayName("Success - Multipart upload")
    void uploadMultipartFileSuccess() {
        // given
        long fileSize = 9 * 1024 * 1024L; // File with a size exceeding 8MB
        byte[] partContent = new byte[5 * 1024 * 1024]; // content (5MB)

        // when
        PresignedUrl presignedUrl = mediaService.initMedia(MediaTypeEnum.VIDEO, fileSize, id);
        assertThat(presignedUrl.getPresignedUrlParts()).hasSize(2); // 9MB / 8MB = 2 parts

        // Upload each part of the image and collect the ETag from the response
        List<MediaService.MultipartUploaded> uploadedParts = new ArrayList<>(); 
        for (PresignedUrl.PresignedUrlPart part : presignedUrl.getPresignedUrlParts()) {
            Response response = RestAssured.given()
                    .header("Content-Type", "video/mp4")
                    .body(partContent)
                    .put(part.getPresignedUrl());

            String etag = response.getHeader("ETag");
            assertThat(etag).isNotNull().isNotEmpty(); // Assert that each part's ETag exists
            uploadedParts.add(new MediaService.MultipartUploaded(part.getPartNumber(), etag));
        }

        // Merge all uploaded parts into a single object
        mediaService.completeMultipartUpload(
                presignedUrl.getMedia().getPath(), 
                presignedUrl.getUploadId(), 
                uploadedParts
        );

        // then
        assertThat(doesObjectExist(presignedUrl.getMedia().getPath())).isTrue();
    }

    @Test
    @DisplayName("Fail - Unmatched 'Content-Type' header")
    void uploadSingleFileFail_InvalidContentType() {
        // given
        PresignedUrl presignedUrl = mediaService.initMedia(MediaTypeEnum.IMAGE, 1024L, id);

        // when
        Response response = RestAssured.given()
                .header("Content-Type", "video/mp4") // Incorrect header with presigned url
                .body("content".getBytes())
                .put(presignedUrl.getPresignedUrl());

        // then
        assertThat(response.getStatusCode()).isEqualTo(403);
    }

    private boolean doesObjectExist(String path) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(minioProperties.bucket())
                    .key(path)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}