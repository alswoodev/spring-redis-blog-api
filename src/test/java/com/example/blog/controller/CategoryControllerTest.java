package com.example.blog.controller;

import com.example.blog.dto.CategoryDTO;
import com.example.blog.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void testRegisterCategory_Success() throws Exception {
        // given
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("IT/기술");
        // categoryService.register() is mocked to succeed
        doNothing().when(categoryService).register(anyString(), anyString());

        // when && then
        mockMvc.perform(post("/categories")
                        .param("accountId", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated());

        verify(categoryService).register(anyString(), anyString());
    }

    @Test
    void testUpdateCategory_Success() throws Exception {
        // given
        Long categoryId = 1L;
        String updateName = "업데이트카테고리";
        // CategoryRequest is a private inner class; using JSON string instead
        String content = "{\"id\":1, \"name\":\"" + updateName + "\"}";

        // categoryService.update() is mocked to succeed
        doNothing().when(categoryService).update(anyLong(), anyString());

        // when && then
        mockMvc.perform(patch("/categories/{categoryId}", categoryId)
                        .param("accountId", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        verify(categoryService).update(1L, updateName);
    }

    @Test
    void testDeleteCategory_Success() throws Exception {
        // given
        Long categoryId = 1L;
        // categoryService.delete() is mocked to succeed
        doNothing().when(categoryService).delete(anyLong());

        // when && then
        mockMvc.perform(delete("/categories/{categoryId}", categoryId)
                        .param("accountId", "admin"))
                .andExpect(status().isOk());

        verify(categoryService).delete(categoryId);
    }
}