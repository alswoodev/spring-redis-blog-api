package com.example.blog.controller;

import com.example.blog.aop.LoginCheck;
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
        UserDTO memberInfo = userService.getUserInfo(userId);

        // Authorization check: ensure the logged-in user is the owner of the post
        if ( postDTO.getUserId() != memberInfo.getId() ) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 게시글을 등록할 수 없는 유저");

        postService.register(userId, postDTO);

        return ResponseEntity.ok(new CommonResponse<PostDTO>(HttpStatus.OK, "SUCCESS", "registerPost", postDTO));
    }

    @GetMapping("my-posts")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<List<PostDTO>>> myPostInfo(Long userId) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = userService.getUserInfo(userId);

        List<PostDTO> postDTOList = postService.findMyPosts(memberInfo.getId());
        
        return ResponseEntity.ok(new CommonResponse<List<PostDTO>>(HttpStatus.OK, "SUCCESS", "myPostInfo", postDTOList));
    }

    @GetMapping("{postId}")
    public ResponseEntity<CommonResponse<PostDTO>> getPostDetail(@PathVariable Long postId){
        PostDTO post = postService.getPostDetail(postId);
        return ResponseEntity.ok(new CommonResponse<PostDTO>(HttpStatus.OK, "SUCCESS", "getPostDetail", post));
    }

    @PatchMapping("{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> updatePosts(Long userId,
                               @PathVariable Long postId,
                               @RequestBody PostDTO postDTO) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = userService.getUserInfo(userId);

        // Authorization check: ensure the logged-in user is the owner of the post
        if ( postDTO.getUserId() != memberInfo.getId() ) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 게시글을 수정할 수 없는 유저");
        
        // Request validation: ensure the post ID in the request body matches the path variable
        if ( postDTO.getId() != postId ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post ID in request body does not match path variable.");
        return ResponseEntity.ok(new CommonResponse<PostDTO>(HttpStatus.OK, "SUCCESS", "updatePost", postDTO));

    }

    @DeleteMapping("{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDeleteRequest>> deleteposts(Long userId,
                               @PathVariable Long postId,
                               @RequestBody PostDeleteRequest request) {
        // Validate that the user exists and retrieve user information
        UserDTO memberInfo = userService.getUserInfo(userId);

        // Authorization check: ensure the logged-in user is the owner of the post
        if ( request.getUserId() != memberInfo.getId() ) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 게시글을 삭제할 수 없는 유저");
        
        // Request validation: ensure the post ID in the request body matches the path variable
        if ( request.getPostId() != postId ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post ID in request body does not match path variable.");
        postService.deletePost(postId);
        return ResponseEntity.ok(new CommonResponse<>(HttpStatus.OK, "SUCCESS", "deletePost", request));
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