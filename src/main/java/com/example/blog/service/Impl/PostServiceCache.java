package com.example.blog.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.blog.code.PostCode;
import com.example.blog.dto.PostDTO;
import com.example.blog.mapper.FileMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.TagMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceCache {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private TagMapper tagMapper;

    @Cacheable(value = "postCache", key="'getPost' + #id")
    public PostDTO getStaticPost(Long id){
        PostDTO postDTO = postMapper.getPost(id);
        if(postDTO == null){
            log.error("get Post ERROR! Invalid postId {}", id);
            throw new com.example.blog.exception.InvalidParameterException("id", PostCode.POST_NOT_FOUND);
        }
        postDTO.setFiles(fileMapper.findByPostId(id));
        postDTO.setTags(tagMapper.findByPostId(id));
        return postDTO;
    }
}
