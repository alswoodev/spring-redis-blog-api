package com.example.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.blog.dto.PostDTO;
import com.example.blog.dto.request.PostSearchRequest;

@Mapper
public interface PostSearchMapper {
    List<PostDTO> findAllByPostSearchRequest(PostSearchRequest postSearchRequest);
}
