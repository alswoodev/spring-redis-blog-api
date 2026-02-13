package com.example.blog.controller;

import com.example.blog.dto.UserDTO;
import com.example.blog.dto.request.UserDeleteId;
import com.example.blog.dto.request.UserLoginRequest;
import com.example.blog.dto.request.UserUpdatePasswordRequest;
import com.example.blog.dto.response.LoginResponse;
import com.example.blog.exception.DuplicateIdException;
import com.example.blog.service.UserService;
import com.example.blog.utils.SessionUtil;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody UserDTO userDTO) {
        // Null validation
        if(UserDTO.hasNullDataBeforeSignup(userDTO)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Register user
        try {
            userService.register(userDTO);
        } catch (DuplicateIdException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest request, 
                                             HttpSession session) {
        UserDTO userInfo = userService.login(request.getUserId(), request.getPassword());
        
        // If user doesn't exist
        if(userInfo == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Separate session logic for admin and regular user
        if (userInfo.getStatus() == UserDTO.Status.ADMIN) {
            SessionUtil.setLoginAdminId(session, userInfo.getUserId());
        } else {
            SessionUtil.setLoginMemberId(session, userInfo.getUserId());
        }

        return new ResponseEntity<>(LoginResponse.success(userInfo), HttpStatus.OK);
    }

     @GetMapping("my-info")
    public ResponseEntity<UserDTO> memberInfo(HttpSession session) {
        // Get regular user's id
        String id = SessionUtil.getLoginMemberId(session);
        if (id == null) id = SessionUtil.getLoginAdminId(session); // Get admin user's id

        // If the ID is not found for either regular or admin user
        if (id == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        UserDTO memberInfo = userService.getUserInfo(id);
        return new ResponseEntity<>(memberInfo, HttpStatus.OK);
    }

    @PatchMapping("password")
    public ResponseEntity<Void> updateUserPassword(@RequestBody UserUpdatePasswordRequest request,
                                        HttpSession session) {
        String id = SessionUtil.getLoginMemberId(session);
        if(id == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // Update password
        try{
            userService.updatePassword(id, request.getBeforePassword(), request.getAfterPassword());
        }catch(IllegalArgumentException e){
            log.error("Fail to update password", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteId(@RequestBody UserDeleteId userDeleteId,
                                       HttpSession session) {
        String id = SessionUtil.getLoginMemberId(session);
        if(id == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // Delete user from DB
        try {
            userService.deleteId(id, userDeleteId.getPassword());
        } catch (RuntimeException e) {
            log.error("Fail to delete user", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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