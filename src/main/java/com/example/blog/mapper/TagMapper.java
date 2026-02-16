package com.example.blog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.blog.dto.TagDTO;

@Mapper
public interface TagMapper {
    public int insertTag(TagDTO tagDTO);

    public int deleteTag(@Param("id") Integer id);

    public List<TagDTO> findByPostId(@Param("postId") Long postId);

    public TagDTO findByName(@Param("name") String name);

    public int insertPostTag(TagDTO tagDTO);
}
