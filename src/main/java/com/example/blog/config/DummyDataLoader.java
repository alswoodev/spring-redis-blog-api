package com.example.blog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.TagDTO;
import com.example.blog.mapper.PostMapper;

import lombok.RequiredArgsConstructor;

// This class generate dummy posts for performance test
/*@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner{
    private final PostMapper postMapper;

    @Override
    public void run(String... args) throws Exception {
        final int TOTAL = 10000;

        for (int i = 1; i <= TOTAL; i++) {
            FileDTO file = FileDTO.builder()
                                .name("file"+i)
                                .path("path"+i)
                                .extension("extension")
                                .build();

            TagDTO tag = TagDTO.builder()
                            .name("tag" + i)
                            .build();

            PostDTO post = PostDTO.builder()
                                .name("test" + i)
                                .contents("A".repeat(10000))
                                .userId(16L)
                                .file(file)
                                .tag(tag).build();

            int count = postMapper.insertPost(post);
            if (count != 1) {
                System.err.println("Failed to insert post: " + post.getName());
            }

            if (i % 1000 == 0) {
                System.out.println(i + " posts inserted");
            }
        }

        System.out.println("Test data loading completed!");
    }
}*/