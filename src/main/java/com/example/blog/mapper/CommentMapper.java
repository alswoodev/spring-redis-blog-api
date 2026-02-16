package com.example.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.blog.dto.CommentDTO;

@Mapper
public interface CommentMapper {
    public int insertComment(CommentDTO commentDTO);

    public List<CommentDTO> findByPostId(@Param("postId") Long postId);

    public int updateComment(CommentDTO commentDTO);

    public int deleteComment(@Param("id") Long id);
}
