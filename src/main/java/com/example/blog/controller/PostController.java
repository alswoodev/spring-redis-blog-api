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
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> registerPost(Long userId, @RequestBody PostDTO postDTO) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        try{postService.register(memberInfo.getId(), postDTO);}
        catch(IllegalArgumentException e){return ResponseEntity.badRequest()
            .body(new CommonResponse<PostDTO>(HttpStatus.BAD_REQUEST, "FAIL", "registerPost", postDTO));}

        return ResponseEntity.ok(new CommonResponse<PostDTO>(HttpStatus.OK, "SUCCESS", "registerPost", postDTO));
    }

    @GetMapping("my-posts")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<List<PostDTO>>> myPostInfo(Long userId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        try{ 
            List<PostDTO> postDTOList = postService.findMyPosts(memberInfo.getId());
            return ResponseEntity.ok(new CommonResponse<List<PostDTO>>(HttpStatus.OK, "SUCCESS", "myPostInfo", postDTOList));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest()
                .body(new CommonResponse<List<PostDTO>>(HttpStatus.BAD_REQUEST, "FAIL", "myPostInfo", null));
        }
    }

    @GetMapping("{postId}")
    public ResponseEntity<CommonResponse<PostDTO>> getPostDetail(@PathVariable Long postId){
        PostDTO post = postService.getPostDetail(postId);
        if (post == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(HttpStatus.OK, "SUCCESS", "getPostDetail", post));
    }

    @PatchMapping
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> updatePosts(Long userId,
                               @RequestBody PostDTO postDTO) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);

        try{postService.updatePost(memberInfo.getId(), postDTO);} 
        catch(IllegalArgumentException e) {return ResponseEntity.badRequest()
            .body(new CommonResponse<PostDTO>(HttpStatus.BAD_REQUEST, "FAIL", "updatePost", postDTO)); }

        return ResponseEntity.ok(new CommonResponse<PostDTO>(HttpStatus.OK, "SUCCESS", "updatePost", postDTO));

    }

    @DeleteMapping("{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDeleteRequest>> deleteposts(Long userId,
                               @PathVariable Long postId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        try{postService.deletePost(memberInfo.getId(), postId);} 
        catch(IllegalArgumentException e) {return ResponseEntity.badRequest()
            .body(new CommonResponse<PostDeleteRequest>(HttpStatus.BAD_REQUEST, "FAIL", "deletePost", null)); }

        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK, "SUCCESS", "deletePost", null));
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
        try{postService.registerComment(request);} 
        catch(IllegalArgumentException e) {return ResponseEntity.badRequest()
            .body(new CommonResponse<CommentDTO>(HttpStatus.BAD_REQUEST, "FAIL", "addComment", null)); }
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(HttpStatus.OK, "SUCCESS", "addComment", request));
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
        try{postService.updateComment(userId, request);} 
        catch(IllegalArgumentException e) {return ResponseEntity.badRequest()
            .body(new CommonResponse<CommentDTO>(HttpStatus.BAD_REQUEST, "FAIL", "updateComment", null)); }
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(HttpStatus.OK, "SUCCESS", "updateComment", null));
    }

    @DeleteMapping("{postId}/comments/{commentId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<CommentDTO>> deleteComment(Long userId,
                               @PathVariable Long postId,
                               @PathVariable Long commentId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = retreiveUser(userId);
        postService.deleteComment(memberInfo.getId(), commentId);
        return ResponseEntity.ok(new CommonResponse<CommentDTO>(HttpStatus.OK, "SUCCESS", "deleteComment", null));
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