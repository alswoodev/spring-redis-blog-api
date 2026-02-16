package com.example.blog.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.blog.dto.PostDTO;
import com.example.blog.dto.request.PostSearchRequest;
import com.example.blog.mapper.PostSearchMapper;
import com.example.blog.service.PostSearchService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostSearchServiceImpl implements PostSearchService {

    @Autowired
    private PostSearchMapper postSearchMapper;

    @Cacheable(value = "postCache", key="'getPosts' + #postSearchRequest.getName() + #postSearchRequest.getCategoryId()")
    @Override
    public List<PostDTO> search(PostSearchRequest postSearchRequest){
        try { return postSearchMapper.findAllByPostSearchRequest(postSearchRequest); }
        catch(RuntimeException e){log.error("Fail to search. Param : "+ postSearchRequest +e.getMessage());}
        return null;
    }
}
