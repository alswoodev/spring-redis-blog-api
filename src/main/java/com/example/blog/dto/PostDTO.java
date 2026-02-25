package com.example.blog.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.blog.code.PostCode;
import com.example.blog.exception.InvalidParameterException;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private boolean isAdmin;
    private String name;
    private String contents;
    private String preview;
    private int views;
    private int commentCount;
    private Date createTime;
    private Date updateTime;

    //FK
    private Integer categoryId;
    private Long userId;
    
    // 1:N Mapping
    private List<FileDTO> files;
    private List<TagDTO> tags;
    private List<CommentDTO> comments;

    // Custom builder pattern for testing purposes
    public static class PostDTOBuilder {
        public PostDTOBuilder file(FileDTO file) {
            if (this.files == null) this.files = new ArrayList<>();
            this.files.add(file);
            return this;
        }

        public PostDTOBuilder tag(TagDTO tag) {
            if (this.tags == null) this.tags = new ArrayList<>();
            this.tags.add(tag);
            return this;
        }

        public PostDTOBuilder comment(CommentDTO comment) {
            if (this.comments == null) this.comments = new ArrayList<>();
            this.comments.add(comment);
            return this;
        }
    }

    public static boolean hasNullData(PostDTO postDTO){
        if( postDTO.getName() == null || postDTO.getName().trim() == "") throw new InvalidParameterException("name", PostCode.POST_NO_TITLE);
        if( postDTO.getUserId() == null ) throw new InvalidParameterException("userId", PostCode.POST_NO_WRITER);
        return false;
    }
}