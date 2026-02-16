package com.example.blog.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private int views;
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
        if( postDTO.getName() == null || postDTO.getContents() == null || postDTO.getUserId() == null ) return true;
        else return false;
    }
}