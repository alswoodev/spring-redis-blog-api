package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.request.PostSearchRequest;

@SpringBootTest
@Transactional
public class PostSearchTest {
    @Autowired
    private PostSearchService postSearchService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    Long userId;

    PostDTO postDTO = new PostDTO();

    @BeforeEach
    void setUp(){
        UserDTO userDTO = UserDTO.builder()
                                .userId("test")
                                .password("test")
                                .nickname("test").build();
        userService.register(userDTO);
        userId = userService.login("test","test").getId();

        PostDTO postDTO = PostDTO.builder()
            .name("title")
            .contents("contents")
            .file(FileDTO.builder()
                        .path("https://1")
                        .name("image1")
                        .extension(".jpg")
                        .build())
            .file(FileDTO.builder()
                        .path("https://2")
                        .name("image2")
                        .extension(".jpg")
                        .build())
            .build();
        
        postService.register(userId, postDTO);
    }

    @Test
    void testSearch(){
        //given
        PostSearchRequest request = new PostSearchRequest();
        request.setName("title");

        //when
        List<PostDTO> result = postSearchService.search(request);

        //then
        assertThat(result.size()).isNotEqualTo(0);
    }

}
