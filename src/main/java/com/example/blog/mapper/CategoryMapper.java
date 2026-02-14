package com.example.blog.mapper;

public interface CategoryMapper {
    public int register(String name);

    public void updateCategory(Long categoryId, String name);

    public void deleteCategory(Long categoryId);

    public Long findByName(String name);
}