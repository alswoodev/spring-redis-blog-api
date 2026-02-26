package com.example.blog.dto.request.post;

import com.example.blog.dto.CommentDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
	@NotEmpty(message="{comment.empty}")
	@Size(max=250, message="{comment.too.long}")
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
