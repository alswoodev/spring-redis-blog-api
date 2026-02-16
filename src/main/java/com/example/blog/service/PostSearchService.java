package com.example.blog.service;

import java.util.List;

import com.example.blog.dto.PostDTO;
import com.example.blog.dto.request.PostSearchRequest;

public interface PostSearchService {
    List<PostDTO> search(PostSearchRequest postSearchRequest);
}
