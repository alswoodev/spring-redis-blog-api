package com.example.blog.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
	private Integer id;

	private String name;

	private Date createTime;

	private Long postId; // PostID is not FK for tags table, but used for consistency

	// Use value-based comparison (equals/hashCode) instead of reference equality
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TagDTO tagDTO = (TagDTO) o;

		return name.equals(tagDTO.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
