package com.example.blog.controller;

import com.example.blog.aop.LoginCheck;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.response.CommonResponse;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> registerPost(Long userId, @RequestBody PostDTO postDTO) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        postService.register(memberInfo.getId(), postDTO);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(true, postDTO));
    }

    @GetMapping("my-posts")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<List<PostDTO>>> myPostInfo(Long userId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        List<PostDTO> postDTOList = postService.findMyPosts(memberInfo.getId());
        return ResponseEntity.ok(new CommonResponse<List<PostDTO>>(true, postDTOList));
    }

    @GetMapping("{postId}")
    public ResponseEntity<CommonResponse<PostDTO>> getPostDetail(@PathVariable Long postId){
        PostDTO post = postService.getPostDetail(postId);
        if (post == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(true, post));
    }

    @PatchMapping
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> updatePosts(Long userId,
                               @RequestBody PostDTO postDTO) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        postService.updatePost(memberInfo.getId(), postDTO);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(true, postDTO));

    }

    @DeleteMapping("{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDeleteRequest>> deleteposts(Long userId,
                               @PathVariable Long postId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        postService.deletePost(memberInfo.getId(), postId);
        return ResponseEntity.ok(new CommonResponse<>(true, null));
    }

    @PostMapping("{postId}/comments")
    @ResponseStatus()
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<CommentDTO>> addComment(Long userId,
                               @PathVariable Long postId,
                               @RequestBody CommentDTO request) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        request.setUserId(memberInfo.getId());
        request.setPostId(postId);
        postService.registerComment(request);
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(true, request));
    }

    @PatchMapping("{postId}/comments/{commentId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<CommentDTO>> updateComment(Long userId,
                               @PathVariable Long postId,
                               @PathVariable Long commentId,
                               @RequestBody CommentDTO request) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        request.setUserId(memberInfo.getId());
        request.setPostId(postId);
        request.setId(commentId);
        postService.updateComment(userId, request);
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(true, null));
    }

    @DeleteMapping("{postId}/comments/{commentId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<CommentDTO>> deleteComment(Long userId,
                               @PathVariable Long postId,
                               @PathVariable Long commentId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        postService.deleteComment(memberInfo.getId(), commentId);
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(true, null));
    }


    private UserDTO retreiveUser(Long userId) {
        UserDTO memberInfo = userService.getUserInfo(userId);
        if (memberInfo == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User information could not be retrieved.");
        return memberInfo;
    }

    // -------------- response 객체 --------------

    @Getter
    @AllArgsConstructor
    private static class PostResponse {
        private List<PostDTO> postDTO;
    }

    @Setter
    @Getter
    private static class PostDeleteRequest {
        private Long userId;
        private Long postId;
    }
}