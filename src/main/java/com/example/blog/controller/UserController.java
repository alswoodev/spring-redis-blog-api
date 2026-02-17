package com.example.blog.controller;

import com.example.blog.aop.LoginCheck;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.request.UserDeleteId;
import com.example.blog.dto.request.UserLoginRequest;
import com.example.blog.dto.request.UserUpdatePasswordRequest;
import com.example.blog.dto.response.LoginResponse;
import com.example.blog.service.UserService;
import com.example.blog.utils.SessionUtil;

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
    public ResponseEntity<Void> signUp(@RequestBody UserDTO userDTO) {
        // Null validation
        if(UserDTO.hasNullDataBeforeSignup(userDTO)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Register user
        userService.register(userDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest request, 
                                             HttpSession session) {
        UserDTO userInfo = userService.login(request.getUserId(), request.getPassword());

        // Separate session logic for admin and regular user
        if (userInfo.getStatus() == UserDTO.Status.ADMIN) {
            SessionUtil.setLoginAdminId(session, userInfo.getId());
        } else {
            SessionUtil.setLoginMemberId(session, userInfo.getId());
        }

        return new ResponseEntity<>(LoginResponse.success(userInfo), HttpStatus.OK);
    }

    @GetMapping("my-info")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<UserDTO> memberInfo(Long id, HttpSession session) {
        // Get admin user's id if login check is failed
        if (id == null) id = SessionUtil.getLoginAdminId(session);

        // Unauthorized check via @LoginCheck annotation

        UserDTO memberInfo = userService.getUserInfo(id);
        return new ResponseEntity<>(memberInfo, HttpStatus.OK);
    }

    @PatchMapping("password")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<Void> updateUserPassword(Long id, @RequestBody UserUpdatePasswordRequest request,
                                        HttpSession session) {
        // Unauthorized check via @LoginCheck annotation

        // Update password
        userService.updatePassword(id, request.getBeforePassword(), request.getAfterPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<Void> deleteId(Long id, @RequestBody UserDeleteId userDeleteId,
                                       HttpSession session) {
        // Unauthorized check via @LoginCheck annotation

        // Delete user from DB
        userService.deleteId(id, userDeleteId.getPassword());
        // Remove session
        try {
            SessionUtil.clear(session);
        } catch (RuntimeException e) {
            log.error("Fail to remove session", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    
    }
}