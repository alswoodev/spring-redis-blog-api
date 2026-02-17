package com.example.blog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.blog.dto.PostDTO;
import com.example.blog.dto.request.PostSearchRequest;
import com.example.blog.dto.response.CommonResponse;
import com.example.blog.service.PostSearchService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Slf4j
@RequestMapping("/search")
@RequiredArgsConstructor
public class PostSearchController {
    private final PostSearchService postSearchService;

    @PostMapping
    public ResponseEntity<CommonResponse<PostSearchResponse>> search(@RequestBody PostSearchRequest request) {
        try { return ResponseEntity.ok(new CommonResponse<PostSearchResponse>(HttpStatus.OK, "SUCCESS", "search", new PostSearchResponse(postSearchService.search(request))));}
        catch(RuntimeException e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST);}
    }

    @GetMapping("/tag")
    public ResponseEntity<CommonResponse<PostSearchResponse>> searchByTagName(@RequestParam String name) {
        try { return ResponseEntity.ok(new CommonResponse<PostSearchResponse>(HttpStatus.OK, "SUCCESS", "searchByTagName", new PostSearchResponse(postSearchService.searchByTagName(name))));}
        catch(RuntimeException e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST);}
    }
    
    @AllArgsConstructor
    @Getter
    @Setter
    private static class PostSearchResponse{
        private List<PostDTO> posts;
    }
}
