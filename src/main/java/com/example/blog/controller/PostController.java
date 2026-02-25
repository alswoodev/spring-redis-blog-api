package com.example.blog.controller;

import com.example.blog.aop.LoginCheck;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.request.post.CommentRequest;
import com.example.blog.dto.request.post.PostRegisterRequest;
import com.example.blog.dto.response.CommonResponse;
import com.example.blog.dto.response.post.PostSummaryResponse;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    @LoginCheck(type = LoginCheck.UserType.USER)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommonResponse<PostDTO>> registerPost(@Parameter(hidden=true) Long userId, 
                                                                @RequestBody PostRegisterRequest request,
                                                                UriComponentsBuilder uriBuilder) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        PostDTO postDTO = request.convert();

        postService.register(memberInfo.getId(), postDTO);
        PostDTO response = postService.getPostDetail(postDTO.getId());

        URI location = uriBuilder.path("/posts/{id}").buildAndExpand(postDTO.getId()).toUri();
        return ResponseEntity
            .created(location)
            .body(new CommonResponse<>(true, response));
    }

    @GetMapping("/mine")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<List<PostSummaryResponse>>> myPostInfo(@Parameter(hidden=true) Long userId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        List<PostDTO> postDTOList = postService.findMyPosts(memberInfo.getId());
        List<PostSummaryResponse> responses = postDTOList.stream()
                                                        .map(PostSummaryResponse::convert)
                                                        .collect(Collectors.toList());
        return ResponseEntity.ok(new CommonResponse<List<PostSummaryResponse>>(true, responses));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CommonResponse<PostDTO>> getPostDetail(@PathVariable Long postId){
        PostDTO post = postService.getPostDetail(postId);
        if (post == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(true, post));
    }

    @PatchMapping
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> updatePosts(@Parameter(hidden=true) Long userId,
                               @RequestBody PostDTO postDTO) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        postService.updatePost(memberInfo.getId(), postDTO);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(true, postDTO));
    }

    @DeleteMapping("/{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteposts(@Parameter(hidden=true) Long userId,
                               @PathVariable Long postId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        postService.deletePost(memberInfo.getId(), postId);
    }

    @PostMapping("/{postId}/comments")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<CommentDTO>> addComment(@Parameter(hidden=true) Long userId,
                               @PathVariable Long postId,
                               @RequestBody CommentRequest request) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        request.setUserId(memberInfo.getId());
        request.setPostId(postId);
        CommentDTO comment = request.convert();
        postService.registerComment(comment);
        CommentDTO response = postService.getCommentDetail(comment.getId());
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(true, response));
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<CommentDTO>> updateComment(@Parameter(hidden=true) Long userId,
                               @PathVariable Long postId,
                               @PathVariable Long commentId,
                               @RequestBody CommentDTO request) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        request.setUserId(memberInfo.getId());
        request.setPostId(postId);
        request.setId(commentId);
        postService.updateComment(userId, request);
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(true, request));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Parameter(hidden=true) Long userId,
                               @PathVariable Long postId,
                               @PathVariable Long commentId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        postService.deleteComment(memberInfo.getId(), commentId);
    }


    private UserDTO retreiveUser(Long userId) {
        UserDTO memberInfo = userService.getUserInfo(userId);
        if (memberInfo == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User information could not be retrieved.");
        return memberInfo;
    }
}