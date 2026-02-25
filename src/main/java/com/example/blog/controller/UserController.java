package com.example.blog.controller;

import com.example.blog.aop.LoginCheck;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.request.user.UserDeleteId;
import com.example.blog.dto.request.user.UserSignInRequest;
import com.example.blog.dto.request.user.UserSignUpRequest;
import com.example.blog.dto.request.user.UserUpdatePasswordRequest;
import com.example.blog.dto.response.CommonResponse;
import com.example.blog.dto.response.user.UserResponse;
import com.example.blog.service.UserService;
import com.example.blog.utils.SessionUtil;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody UserSignUpRequest request) {
        UserDTO user = UserDTO.builder().userId(request.getUserId()).password(request.getPassword()).nickname(request.getNickname()).build();
        userService.register(user);
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody UserSignInRequest request, 
                                             HttpSession session) {
        UserDTO user = userService.login(request.getUserId(), request.getPassword());

        // Separate session logic for admin and regular user
        if (user.getStatus() == UserDTO.Status.ADMIN) {
            SessionUtil.setLoginAdminId(session, user.getId());
        } else {
            SessionUtil.setLoginMemberId(session, user.getId());
        }
    }

    @GetMapping("/me")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<UserResponse>> memberInfo(@Parameter(hidden=true) Long id, HttpSession session) {
        // Get admin user's id if login check is failed
        if (id == null) id = SessionUtil.getLoginAdminId(session);

        // Unauthorized check via @LoginCheck annotation

        UserDTO memberInfo = userService.getUserInfo(id);
        UserResponse response = new UserResponse(id, memberInfo.getUserId(), memberInfo.getUserId());
        return ResponseEntity.ok(new CommonResponse<UserResponse>(true, response));
    }

    @PatchMapping("/me/password")
    @LoginCheck(type = LoginCheck.UserType.USER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserPassword(@Parameter(hidden=true) Long id, @RequestBody UserUpdatePasswordRequest request,
                                        HttpSession session) {
        // Unauthorized check via @LoginCheck annotation

        // Update password
        userService.updatePassword(id, request.getBeforePassword(), request.getAfterPassword());
    }

    @DeleteMapping("/me")
    @LoginCheck(type = LoginCheck.UserType.USER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteId(@Parameter(hidden=true) Long id, @RequestBody UserDeleteId userDeleteId,
                                       HttpSession session) {
        // Unauthorized check via @LoginCheck annotation

        // Delete user from DB
        userService.deleteId(id, userDeleteId.getPassword());
        // Remove session
        try {
            SessionUtil.clear(session);
        } catch (RuntimeException e) {
            log.error("Fail to remove session", e);
            throw new RuntimeException("Fail to remove session");
        }
    }
}