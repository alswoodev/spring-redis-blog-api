package com.example.blog.mapper;

import com.example.blog.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    public int insertPost(PostDTO postDTO);

    public List<PostDTO> findAllByUserId(Long userId);

    public PostDTO getPost(Long id);

    public void updatePost(PostDTO postDTO);

    public void deletePost(Long id);
}