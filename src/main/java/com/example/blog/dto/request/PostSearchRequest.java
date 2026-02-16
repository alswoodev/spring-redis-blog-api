package com.example.blog.dto.request;

import com.example.blog.dto.CategoryDTO.SortStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PostSearchRequest {
    private String name;
    private String contents;
    private int categoryId;
    private SortStatus sortStatus;
}
