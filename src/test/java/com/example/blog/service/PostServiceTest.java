package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.TagDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.mapper.FileMapper;

@SpringBootTest
@Transactional
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileMapper fileMapper;

    Long userId;

    PostDTO postDTO = new PostDTO();

    @BeforeEach
    void setUp(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId("test");
        userDTO.setPassword("test");
        userDTO.setNickname("test");
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
            .tag(TagDTO.builder()
                        .name("tag1")
                        .build())
            .tag(TagDTO.builder()
                        .name("tag2")
                        .build())
            .build();
        
        postService.register(userId, postDTO); // Also test the attach logic
    }
    

    @Test
    void testfindMyProducts(){
        //then
        assertThat(postService.findMyPosts(userId).size()).isEqualTo(1);
    }

    @Test
    void testGetPostDetail(){
        //given
        Long id = postService.findMyPosts(userId).get(0).getId();
        
        //when
        PostDTO postDTO = postService.getPostDetail(id);

        //then
        assertThat(postDTO.getName()).isEqualTo("title");
        assertThat(postDTO.getContents()).isEqualTo("contents");
        assertThat(postDTO.getUserId()).isEqualTo(userId);
        assertThat(postDTO.getFiles().size()).isEqualTo(2); // attachFiles() test
        assertThat(postDTO.getTags().size()).isEqualTo(2); // attachTags() test
    }

    // Also test the updateFiles logic
    @Test
    void testUpdatePost(){
        //given
        Long id = postService.findMyPosts(userId).get(0).getId();  
        PostDTO postDTO = postService.getPostDetail(id);
        postDTO.setName("TITLE");
        postDTO.setContents("CONTENTS");
        postDTO.setFiles(Collections.singletonList(postDTO.getFiles().remove(1)));
        postDTO.setTags(Collections.singletonList(postDTO.getTags().remove(1)));

        //when
        postService.updatePost(postDTO);

        //then
        assertThat(postService.findMyPosts(userId).size()).isEqualTo(1); // Ensure the total count remains the same 
        PostDTO post = postService.getPostDetail(postService.findMyPosts(userId).get(0).getId());
        assertThat(post.getName()).isEqualTo("TITLE");
        assertThat(post.getContents()).isEqualTo("CONTENTS");
        assertThat(post.getFiles().size()).isEqualTo(1); // updateFiles() test
        assertThat(post.getTags().size()).isEqualTo(1); // updateTags() test
    }

    @Test
    void testDeletePost(){
        //given
        Long id = postService.findMyPosts(userId).get(0).getId();

        //when
        postService.deletePost(id);

        //then
        assertThat(postService.findMyPosts(userId).size()).isEqualTo(0);
        assertThat(fileMapper.findByPostId(id).size()).isEqualTo(0);
    }
}
