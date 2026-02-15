package com.example.blog.controller;

import com.example.blog.dto.UserDTO;
import com.example.blog.dto.request.UserDeleteId;
import com.example.blog.dto.request.UserLoginRequest;
import com.example.blog.dto.request.UserUpdatePasswordRequest;
import com.example.blog.exception.DuplicateIdException;
import com.example.blog.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUserId("testUser");
        userDTO.setPassword("password123");
        userDTO.setNickname("TEST");
        userDTO.setAdmin(false);
        userDTO.setCreateTime(new Date());
        userDTO.setStatus(UserDTO.Status.DEFAULT);

        loginRequest = new UserLoginRequest();
        loginRequest.setUserId("testUser");
        loginRequest.setPassword("password123");
    }

    @Test
    void testSignUp_Success() throws Exception {
        //given
        // userService.register() is mocked to succeed
        doNothing().when(userService).register(any(UserDTO.class));

        //when && then
        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testSignUp_DuplicateId() throws Exception {
        //given
        // userService.register() is mocked to throw DuplicateIdException (simulate duplicate ID)
        doThrow(new DuplicateIdException(null)).when(userService).register(any(UserDTO.class));

        //when && then
        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        //given
        // userService.login() is mocked to succeed
        when(userService.login("testUser", "password123")).thenReturn(userDTO);

        //when && then
        mockMvc.perform(post("/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDTO.userId").value("testUser"));
    }

    @Test
    void testLogin_Fail() throws Exception {
        //given
        // userService.login() is mocked to return null (simulate invalid id, pw)
        when(userService.login("testUser", "password123")).thenReturn(null);

        //when && then
        mockMvc.perform(post("/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMemberInfo_Success() throws Exception {
        //given
        // userService.getUserInfo() is mocked to succeed
        when(userService.getUserInfo(any(Long.class))).thenReturn(userDTO);

        //when && then
        mockMvc.perform(get("/users/my-info")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("testUser"));
    }

    @Test
    void testUpdateUserPassword_Success() throws Exception {
        //given
        // Prepare a valid request object
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest();
        request.setBeforePassword("password123");
        request.setAfterPassword("newPassword");

        // userService.updatePassword() is mocked to succeed
        doNothing().when(userService).updatePassword(anyLong(), anyString(), anyString());

        //when && then
        mockMvc.perform(patch("/users/password")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserPassword_Fail() throws Exception {
        //given
        // Prepare a invalid request object
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest();
        request.setBeforePassword("wrong");
        request.setAfterPassword("new");

        // userService.updatePassword() is mocked to throw IllegalArgumentException (simulate invalid before password)
        doThrow(new IllegalArgumentException()).when(userService)
                .updatePassword(anyLong(), anyString(), anyString());

        //when && then
        mockMvc.perform(patch("/users/password")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteId_Success() throws Exception {
        //given
        // Prepare a valid request object
        UserDeleteId request = new UserDeleteId();
        request.setPassword("password123");

        // userService.deleteId() is mocked to succeed
        doNothing().when(userService).deleteId(anyLong(), anyString());

        //when && then
        mockMvc.perform(delete("/users")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteId_Error() throws Exception {
        //given
        // Prepare a invalid request object
        UserDeleteId request = new UserDeleteId();
        request.setPassword("password123");

        // userService.deleteId() is mocked to throw RuntimeException (simulate invalid password)
        doThrow(new RuntimeException()).when(userService).deleteId(anyLong(), anyString());

        //when && then
        mockMvc.perform(delete("/users")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}