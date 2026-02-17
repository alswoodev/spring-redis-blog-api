package com.example.blog.service;

import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.PostDTO;

import java.util.List;

public interface PostService {

    void register(Long userId, PostDTO postDTO);

    List<PostDTO> findMyPosts(Long userId);

    PostDTO getPostDetail(Long id);

    void updatePost(Long userId,PostDTO postDTO);

    void deletePost(Long userId, Long id);

    void registerComment(CommentDTO commentDTO);

    CommentDTO getCommentDetail(Long commentId);

    void updateComment(Long userId, CommentDTO commentDTO);

    void deleteComment(Long userId, Long commentId);
}