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

    private Long id;

    @BeforeEach
    void setUp() {
        //given
        // Generate test user dto
        testUser = new UserDTO();
        testUser.setUserId("testuser");
        testUser.setPassword("password123");
        testUser.setNickname("테스터");
        testUser.setStatus(Status.DEFAULT);
        testUser.setCreateTime(new Date());
        testUser.setAdmin(false);

        userService.register(testUser);
        id = userService.login("testuser", "password123").getId();
    }

    @Test
    void registerDuplicateIdThrowsException() {
        //given
        // Use an already existing userId
        UserDTO duplicateUser = new UserDTO();
        duplicateUser.setUserId("testuser");
        duplicateUser.setPassword("newpass");
        duplicateUser.setNickname("중복테스터");
        duplicateUser.setStatus(Status.DEFAULT);
        duplicateUser.setCreateTime(new Date());
        duplicateUser.setAdmin(false);

        //when & then
        assertThatThrownBy(() -> userService.register(duplicateUser))
                .isInstanceOf(DuplicateIdException.class)
                .hasMessageContaining("중복된 아이디입니다.");
    }

    @Test
    void LoginSuccessfully() {
        //when
        // Login with test user
        UserDTO savedUser = userService.login("testuser", "password123");

        //then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo(SHA256Util.encryptSHA256("password123"));
        assertThat(savedUser.getNickname()).isEqualTo("테스터");
    }

    @Test
    void loginWithWrongPasswordReturnsNull() {
        //when
        // Attempt login with incorrect password
        UserDTO loginUser = userService.login("testuser", "wrongpassword");

        //then
        // Fail to login (return null)
        assertThat(loginUser).isNull();
    }

    @Test
    void updatePasswordSuccessfully() {
        //when
        // Change password
        userService.updatePassword(id, "password123", "newpassword123");

        //then
        // Fail to login with before password
        assertThat(userService.login("testuser", "password123")).isNull();
        // Success to login with changed password
        assertThat(userService.login("testuser", "newpassword123")).isNotNull();
    }

    @Test
    void updatePasswordWithWrongCurrentPasswordThrowsException() {
        //when & then
        // Attempt to change password with incorrect before password
        assertThatThrownBy(() -> userService.updatePassword(id, "wrongpassword", "newpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid passwrod ERROR");
    }

    @Test
    void deleteUserSuccessfully() {
        //when
        // Delete user with correct password
        userService.deleteId(id, "password123");

        //then
        // Deleted user can no longer be found
        assertThat(userService.getUserInfo(id)).isNull();
    }

    @Test
    void deleteUserWithWrongPasswordThrowsException() {
        //when & then
        // Attempt to delete user with incorrect password
        assertThatThrownBy(() -> userService.deleteId(id, "wrongpassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid password ERROR");
    }
}
