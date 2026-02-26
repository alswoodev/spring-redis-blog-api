package com.example.blog.dto.request.post;

import com.example.blog.dto.FileDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequest {
    @NotNull(message="{file.no.url}")
    private String path;
    @NotNull(message="{file.no.name}")
    private String name;
    @NotNull(message="{file.no.extension}")
    private String extension;

    public FileDTO convert(){
        return FileDTO.builder()
                    .path(path)
                    .name(name)
                    .extension(extension)
                    .build();
    }
}
