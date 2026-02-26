package com.example.blog.dto.request.post;

import com.example.blog.dto.TagDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {
    @NotNull(message="{tag.no.name}")
    @NotEmpty(message="{tag.no.name}")
    @Size(min=2, message="{tag.too.short}")
    @Size(max=10, message="{tag.too.long}")
    @Pattern(regexp="^\\S+$", message="{tag.no.space}")
    private String name;

    public TagDTO convert(){
        return TagDTO.builder()
                    .name(name)
                    .build();
    }
}