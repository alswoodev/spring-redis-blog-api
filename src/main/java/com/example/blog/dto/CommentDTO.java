package com.example.blog.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
	private Long id;

	private String contents;
	private Long subCommentId;

	private Date createTime;
	private Date updateTime;

	// FK
	private Long postId;
	private Long userId;
}
