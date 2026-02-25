package com.example.blog.dto.response.post;

import java.util.Date;

import com.example.blog.dto.PostDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostSummaryResponse {
    private Long id;
    @Builder.Default
    private boolean isAdmin = false;
    private String name;
    private String preview;
    private int commentCount;
    private int views;
    private Date createTime;
    private Date updateTime;

    //FK
    private Integer categoryId;
    private Long userId;

    public static PostSummaryResponse convert(PostDTO post){
        return PostSummaryResponse.builder()
                                .id(post.getId())
                                .isAdmin(post.isAdmin())
                                .name(post.getName())
                                .preview(post.getPreview())
                                .views(post.getViews())
                                .commentCount(post.getCommentCount())
                                .createTime(post.getCreateTime())
                                .updateTime(post.getUpdateTime())
                                .categoryId(post.getCategoryId())
                                .userId(post.getUserId())
                                .build();
    }
}
