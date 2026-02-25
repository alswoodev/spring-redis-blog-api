package com.example.blog.dto.request.post;

import com.example.blog.dto.TagDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {
    private String name;

    public TagDTO convert(){
        return TagDTO.builder()
                    .name(name)
                    .build();
    }
}