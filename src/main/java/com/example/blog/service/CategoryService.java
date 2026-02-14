package com.example.blog.service;

public interface CategoryService {

    Long getByName(String name);

    void register(String accountId, String name);

    void update(Long categoryId, String name);

    void delete(Long categoryId);
}