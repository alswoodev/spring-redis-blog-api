package com.example.blog.dto.request.post;

import com.example.blog.dto.FileDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequest {
    private String path;
    private String name;
    private String extension;

    public FileDTO convert(){
        return FileDTO.builder()
                    .path(path)
                    .name(name)
                    .extension(extension)
                    .build();
    }
}
