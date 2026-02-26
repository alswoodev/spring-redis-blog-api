package com.example.blog.dto.request.media;

import com.example.blog.dto.MediaTypeEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MediaInitRequest{
    @Pattern(regexp = "IMAGE|VIDEO", message = "{file.invalid.extension}")
    private MediaTypeEnum mediaType;
    @NotNull(message="{file.no.size}")
    private Long fileSize;
}