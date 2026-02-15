package com.example.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.blog.dto.FileDTO;

public interface FileMapper {
    public int insertFile(FileDTO fileDTO);

    public List<FileDTO> findByPostId(@Param("postId") Long postId);

    public int deleteFile(@Param("id") Integer id);
}
