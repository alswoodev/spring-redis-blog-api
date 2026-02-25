package com.example.blog.service;

import com.example.blog.dto.UserDTO;
import com.example.blog.dto.UserDTO.Status;
import com.example.blog.exception.DuplicateIdException;
import com.example.blog.exception.InvalidParameterException;
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
        testUser = UserDTO.builder()
                .userId("testuser")
                .password("password123")
                .nickname("테스터").build();

        userService.register(testUser);
        id = userService.login("testuser", "password123").getId();
    }

    @Test
    void registerDuplicateIdThrowsException() {
        //given
        // Use an already existing userId
        UserDTO duplicateUser = UserDTO.builder()
                                    .userId("testuser")
                                    .password("password123")
                                    .nickname("중복테스터").build();
        duplicateUser.setStatus(Status.DEFAULT);
        duplicateUser.setCreateTime(new Date());
        duplicateUser.setAdmin(false);

        //when & then
        assertThatThrownBy(() -> userService.register(duplicateUser))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("user.already.exists");
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
        //when & then
        assertThatThrownBy(() -> userService.login("testuser", "wrongpassword"))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("incorrect.password");
    }

    @Test
    void updatePasswordSuccessfully() {
        //when
        // Change password
        userService.updatePassword(id, "password123", "newpassword123");

        //then
        // Fail to login with before password
        assertThatThrownBy(() -> userService.login("testuser", "password123"))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("incorrect.password");
        // Success to login with changed password
        assertThat(userService.login("testuser", "newpassword123")).isNotNull();
    }

    @Test
    void updatePasswordWithWrongCurrentPasswordThrowsException() {
        //when & then
        // Attempt to change password with incorrect before password
        assertThatThrownBy(() -> userService.updatePassword(id, "wrongpassword", "newpassword"))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("incorrect.password");
    }

    @Test
    void deleteUserSuccessfully() {
        //when
        // Delete user with correct password
        userService.deleteId(id, "password123");

        //then
        // Deleted user can no longer be found
        assertThatThrownBy(() -> userService.getUserInfo(id))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("user.not.found");
    }

    @Test
    void deleteUserWithWrongPasswordThrowsException() {
        //when & then
        // Attempt to delete user with incorrect password
        assertThatThrownBy(() -> userService.deleteId(id, "wrongpassword"))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("incorrect.password");
    }
}
