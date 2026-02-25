package com.example.blog.dto.request.post;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.TagDTO;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PostRegisterRequest {
    @NonNull
    private String name;

    @NonNull
    private String contents;

    private Integer categoryId;

    @NonNull
    private Long userId;
    
    // 1:N Mapping
    private List<FileRequest> files;
    private List<TagRequest> tags;

    public PostDTO convert(){
        List<FileDTO> fileDTOs = files.stream()
                                    .map(file -> file.convert())  // 반환값 있는 메서드
                                    .collect(Collectors.toList());

        List<TagDTO> tagDTOs = tags.stream()
                                .map(tag -> tag.convert())
                                .collect(Collectors.toList());

        return PostDTO.builder()
                    .name(name)
                    .contents(contents)
                    .categoryId(categoryId)
                    .userId(userId)
                    .files(fileDTOs)
                    .tags(tagDTOs)
                    .build();
    }
}
