package com.example.blog.controller;

import com.example.blog.dto.PostDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;
    private PostDTO postDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();

        // Standalone setup to test only the controller logic
        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .build();

        // Initialize sample UserDTO
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUserId("testUser");

        // Initialize sample PostDTO
        postDTO = new PostDTO();
        postDTO.setId(100L);
        postDTO.setUserId(1L);
        postDTO.setName("Test Title");
        postDTO.setContents("Test Contents");
    }

    @Test
    @DisplayName("Register Post - Success")
    void testRegisterPost_Success() throws Exception {
        // given
        // Mock user service to return valid member info
        when(userService.getUserInfo(anyLong())).thenReturn(userDTO);
        // Mock post service registration
        doNothing().when(postService).register(anyLong(), any(PostDTO.class));

        // when & then
        mockMvc.perform(post("/posts")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.body.name").value("Test Title"));
    }

    @Test
    @DisplayName("Register Post - Unauthorized (User Mismatch)")
    void testRegisterPost_Unauthorized() throws Exception {
        // given
        // PostDTO belongs to user 999, but session user is 1
        postDTO.setUserId(999L);
        when(userService.getUserInfo(1L)).thenReturn(userDTO);

        // when & then
        mockMvc.perform(post("/posts")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get My Posts - Success")
    void testMyPostInfo_Success() throws Exception {
        // given
        List<PostDTO> list = Collections.singletonList(postDTO);
        when(userService.getUserInfo(1L)).thenReturn(userDTO);
        when(postService.findMyPosts(1L)).thenReturn(list);

        // when & then
        mockMvc.perform(get("/posts/my-posts")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].id").value(100));
    }

    @Test
    @DisplayName("Update Post - Success")
    void testUpdatePost_Success() throws Exception {
        // given
        when(userService.getUserInfo(1L)).thenReturn(userDTO);

        // when & then
        mockMvc.perform(patch("/posts/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(100));
    }

    @Test
    @DisplayName("Update Post - Bad Request (Post ID Mismatch)")
    void testUpdatePost_BadRequest_IdMismatch() throws Exception {
        // given
        when(userService.getUserInfo(1L)).thenReturn(userDTO);
        
        // Path variable is 100, but DTO id is 200
        PostDTO mismatchDTO = new PostDTO();
        mismatchDTO.setId(200L);
        mismatchDTO.setUserId(1L);

        // when & then
        mockMvc.perform(patch("/posts/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mismatchDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete Post - Success")
    void testDeletePost_Success() throws Exception {
        // given
        when(userService.getUserInfo(1L)).thenReturn(userDTO);
        doNothing().when(postService).deletePost(100L);

        // Constructing JSON for PostDeleteRequest (private inner class)
        String requestBody = "{\"userId\":1, \"postId\":100}";

        // when & then
        mockMvc.perform(delete("/posts/100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        verify(postService, times(1)).deletePost(100L);
    }
}