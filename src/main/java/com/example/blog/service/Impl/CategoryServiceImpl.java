package com.example.blog.service.Impl;

import com.example.blog.mapper.CategoryMapper;
import com.example.blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Long getByName(String name){
        if(name!=null){
            Long id = categoryMapper.findByName(name);
            return id;
        } else {
            log.error("register ERROR! {}", name);
            throw new RuntimeException("register ERROR! 해당 카테고리가 존재하지 않습니다\n" + "Params : " + name);
        }
    }

    @Override
    public void register(String accountId, String name) {
        if (accountId != null) {
            categoryMapper.register(name);
        } else {
            log.error("register ERROR! {}", name);
            throw new RuntimeException("register ERROR! 유저 ID가 존재하지 않습니다\n" + "Params : " + name);
        }
    }

    @Override
    public void update(Long categoryId, String name) {
        if (categoryId != 0) {
            categoryMapper.updateCategory(categoryId, name);
        } else {
            log.error("update ERROR! {}", name);
            throw new RuntimeException("update ERROR! 물품 카테고리 변경 메서드를 확인해주세요\n" + "Params : " + name);
        }
    }

    @Override
    public void delete(Long categoryId) {
        if (categoryId != 0) {
            categoryMapper.deleteCategory(categoryId);
        } else {
            log.error("deleteCategory ERROR! {}", categoryId);
            throw new RuntimeException("deleteCategory ERROR! 물품 카테고리 삭제 메서드를 확인해주세요\n" + "Params : " + categoryId);
        }
    }
}