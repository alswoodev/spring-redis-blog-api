package com.example.blog.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CategoryServiceTestImpl {
    @Autowired
    private CategoryService categoryService;

    String accountId = "test";
    String name = "개발";
    Long id;

    @BeforeEach
    void setUp(){
        categoryService.register(accountId, name);
        id = categoryService.getByName(name);
    }

    @Test
    void testRegisterSuccessfully(){
        //given
        String name = "경제";

        //when
        categoryService.register(accountId, name);

        //then
        assertThat(categoryService.getByName(name)).isNotNull();
    }

    @Test
    void testUpdateSuccessfully(){
        //given
        String name2 = "개발2";

        //when
        System.err.println(id);
        categoryService.update(id, name2);

        //then
        assertThat(categoryService.getByName(name2)).isNotNull();
    }

    @Test
    void testDeleteSuccessfully(){
        //given
        assertThat(categoryService.getByName(name)).isNotNull();
        
        //when
        categoryService.delete(id);

        //then
        assertThat(categoryService.getByName(name)).isNull();
    }
}
