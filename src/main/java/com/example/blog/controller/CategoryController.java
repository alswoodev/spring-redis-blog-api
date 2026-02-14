package com.example.blog.controller;

import com.example.blog.aop.LoginCheck;
import com.example.blog.dto.CategoryDTO;
import com.example.blog.service.CategoryService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Log4j2
public class CategoryController {

    private final CategoryService categoryService;

    // Temporary func to test
    @GetMapping
    @LoginCheck(type = LoginCheck.UserType.ADMIN)
    public CategoryRequest getCategoryId(String accountId, 
                                 @RequestParam String categoryName) {
        Long id = categoryService.getByName(categoryName);
        if(id==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }   
        return new CategoryRequest(id, categoryName);
    }
    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck(type = LoginCheck.UserType.ADMIN)
    public void registerCategory(String accountId, @RequestBody CategoryRequest categoryRequest) {
        categoryService.register(accountId, categoryRequest.getName());
    }

    @PatchMapping("{categoryId}")
    @LoginCheck(type = LoginCheck.UserType.ADMIN)
    public void updateCategories(String accountId,
                                 @PathVariable Long categoryId,
                                 @RequestBody CategoryRequest categoryRequest) {
        categoryService.update(categoryId, categoryRequest.getName());
    }

    @DeleteMapping("{categoryId}")
    @LoginCheck(type = LoginCheck.UserType.ADMIN)
    public void updateCategories(String accountId,
                                 @PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }

    // -------------- request 객체 --------------

    @Setter
    @Getter
    @AllArgsConstructor
    private static class CategoryRequest {
        private Long id;
        private String name;
    }

}