package com.example.blog.service;

import com.example.blog.dto.UserDTO;
import com.example.blog.dto.UserDTO.Status;
import com.example.blog.exception.DuplicateIdException;
import com.example.blog.utils.SHA256Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        //given
        // Generate test user dto
        testUser = new UserDTO();
        testUser.setUserId("testuser");
        testUser.setPassword("password123");
        testUser.setNickName("테스터");
        testUser.setStatus(Status.DEFAULT);
        testUser.setCreateTime(new Date());
        testUser.setAdmin(false);
        testUser.setWithDraw(false);
    }

    @Test
    void registerAndGetUserInfo() {
        //when
        // Register test user
        userService.register(testUser);

        //then
        // Retrieve user information and validate
        UserDTO savedUser = userService.getUserInfo("testuser");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo(SHA256Util.encryptSHA256("password123"));
        assertThat(savedUser.getNickName()).isEqualTo("테스터");
    }

    @Test
    void registerDuplicateIdThrowsException() {
        //given
        // Insert existing user
        userService.register(testUser);

        // Use an already existing userId
        UserDTO duplicateUser = new UserDTO();
        duplicateUser.setUserId("testuser");
        duplicateUser.setPassword("newpass");
        duplicateUser.setNickName("중복테스터");
        duplicateUser.setStatus(Status.DEFAULT);
        duplicateUser.setCreateTime(new Date());
        duplicateUser.setAdmin(false);
        duplicateUser.setWithDraw(false);

        //when & then
        assertThatThrownBy(() -> userService.register(duplicateUser))
                .isInstanceOf(DuplicateIdException.class)
                .hasMessageContaining("중복된 아이디입니다.");
    }

    @Test
    void loginWithCorrectCredentials() {
        //given
        userService.register(testUser);

        //when
        // Login
        UserDTO loginUser = userService.login("testuser", "password123");

        //then
        // Success to login
        assertThat(loginUser).isNotNull();
        assertThat(loginUser.getUserId()).isEqualTo("testuser");
    }

    @Test
    void loginWithWrongPasswordReturnsNull() {
        //given
        userService.register(testUser);

        //when
        // Attempt login with incorrect password
        UserDTO loginUser = userService.login("testuser", "wrongpassword");

        //then
        // Fail to login (return null)
        assertThat(loginUser).isNull();
    }

    @Test
    void updatePasswordSuccessfully() {
        //given
        userService.register(testUser);

        //when
        // Change password
        userService.updatePassword("testuser", "password123", "newpassword123");

        //then
        // Fail to login with before password
        assertThat(userService.login("testuser", "password123")).isNull();
        // Success to login with changed password
        assertThat(userService.login("testuser", "newpassword123")).isNotNull();
    }

    @Test
    void updatePasswordWithWrongCurrentPasswordThrowsException() {
        //given
        userService.register(testUser);

        //when & then
        // Attempt to change password with incorrect before password
        assertThatThrownBy(() -> userService.updatePassword("testuser", "wrongpassword", "newpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("updatePasswrod ERROR");
    }

    @Test
    void deleteUserSuccessfully() {
        //given
        userService.register(testUser);

        //when
        // Delete user with correct password
        userService.deleteId("testuser", "password123");

        //then
        // Deleted user can no longer be found
        assertThat(userService.getUserInfo("testuser")).isNull();
    }

    @Test
    void deleteUserWithWrongPasswordThrowsException() {
        //given
        userService.register(testUser);

        //when & then
        // Attempt to delete user with incorrect password
        assertThatThrownBy(() -> userService.deleteId("testuser", "wrongpassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("deleteId ERROR");
    }
}
