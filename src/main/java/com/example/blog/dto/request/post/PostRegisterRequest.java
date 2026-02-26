package com.example.blog.dto.request.post;

import java.util.List;
import java.util.stream.Collectors;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.TagDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRegisterRequest {
    @NotNull(message="{post.no.title}")
    private String name;

    private String contents;

    private Integer categoryId;

    @NotNull(message="{post.no.writer}")
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
