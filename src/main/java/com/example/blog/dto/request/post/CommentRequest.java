package com.example.blog.dto.request.post;

import com.example.blog.dto.CommentDTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
	private String contents;
	private Long subCommentId;

	// FK
	private Long postId;
	private Long userId;

	public CommentDTO convert(){
		return CommentDTO.builder()
						.contents(contents)
						.subCommentId(subCommentId)
						.postId(postId)
						.userId(userId)
						.build();
	}
}
