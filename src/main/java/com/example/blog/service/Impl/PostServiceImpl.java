package com.example.blog.service.Impl;

import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.mapper.FileMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.PostService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userProfileMapper;

    @Autowired
    private FileMapper fileMapper;

    @CacheEvict(value="getProducts", allEntries = true)
    @Override
    public void register(Long userId, PostDTO postDTO) {
        // Validate userId
        UserDTO memberInfo = userProfileMapper.getUserProfile(userId);

        if (memberInfo != null) {
            postDTO.setUserId(memberInfo.getId());
            if(PostDTO.hasNullData(postDTO) == true) throw new IllegalArgumentException("제목, 내용은 필수 입력값입니다.");
            postMapper.insertPost(postDTO);
        } else {
            log.error("Failed to load user {}", userId);
            throw new IllegalArgumentException("Failed to load user\n" + "Params : " + userId+ postDTO);
        }

        // Attach files
        attachFiles(postDTO);
    }

    // This method doesn't have detail information (e.g. files)
    @Override
    public List<PostDTO> findMyPosts(Long userId) {
        if(userId != null && userId != 0) return postMapper.findAllByUserId(userId);
        else{
            log.error("find Post ERROR! Invalid userId {}", userId);
            throw new IllegalArgumentException("find Post ERROR! Invalid userId" + "Param : "+userId);
        }
    }

    @Override 
    public PostDTO getPostDetail(Long id){
        if(id != null && id != 0) return postMapper.getPost(id);
        else{
            log.error("get Post ERROR! Invalid postId {}", id);
            throw new IllegalArgumentException("get Post ERROR! Invalid postId\n" + "Param : " + id);
        }
    }

    @Override
    @Transactional
    public void updatePost(PostDTO postDTO) {
        if (postDTO != null && postDTO.getId() != 0 && postDTO.getUserId() != 0) {
            int count = postMapper.updatePost(postDTO);
            if (count != 1) throw new IllegalArgumentException("Failed to update post : " + postDTO);
            // Update files table (1:N)
            updateFiles(postDTO);
        } else {
            log.error("update Post ERROR! {}", postDTO);
            throw new IllegalArgumentException("update Post ERROR! 물품 변경 메서드를 확인해주세요\n" + "Params : " + postDTO);
        }
    }

    @Override
    public void deletePost(Long id) {
        if (id != 0) {
            int count = postMapper.deletePost(id);
            if(count != 1) throw new IllegalArgumentException("Failed to delete post id : " + id);
            // Files will be deleted automatically via cascade
        } else {
            log.error("delete Post ERROR! {}", id);
            throw new IllegalArgumentException("update Post ERROR! 물품 삭제 메서드를 확인해주세요\n" + "Params : " + id);
        }
    }

    public void attachFiles(PostDTO postDTO){
        Long postId = postDTO.getId();
        List<FileDTO> files = postDTO.getFiles();
        try{
            // Assign the postId to each FileDTO and persist them
            files.stream()
                .peek(file -> file.setPostId(postId))
                .forEach(file -> {
                        int result = fileMapper.insertFile(file);
                        if (result != 1) {
                            throw new IllegalArgumentException("Failed to insert file: " + file);
                        }
                    });
        } catch(IllegalArgumentException e){
            throw e;
        }
        catch (RuntimeException e){
            log.error("Attach Files ERROR! {}", files);
            throw new IllegalStateException("Invalid Files for Post" + files + e.getMessage());
        }
    }

    // PostDTO must contain a valid post ID before updating files
    public void updateFiles(PostDTO postDTO){
        Long postId = postDTO.getId();
        if(postId == null || postId == 0) throw new IllegalArgumentException("PostID is required to update files");
        // Remaining files
        List<FileDTO> currentFiles = postDTO.getFiles();

        List<FileDTO> existingFiles = fileMapper.findByPostId(postId);

        // If currentFiles is null (not an empty list), treat it as invalid input and skip processing
        if (currentFiles == null) throw new IllegalArgumentException("Files field is null");
        try {
            // * Delete removed files
            // Remove files that exist in DB but are no longer present in the updated DTO
            // (Comparison is based on equals/hashCode - value-based comparison)
            existingFiles.stream()
                .filter(file -> !currentFiles.contains(file))  // Select existingFiles that currentFiles don't have
                .forEach(file -> {
                        int result = fileMapper.deleteFile(file.getId());
                        if (result != 1) {
                            throw new IllegalArgumentException("Failed to delete file: " + file);
                        }
                    }); 

            // * Insert newly added files
            // Add files that are present in the updated DTO but not yet stored in DB
            // Set postId and creation time before inserting
            currentFiles.stream()
                    .filter(file -> !existingFiles.contains(file)) // Select currentFiles that existingFiles don't have
                    .peek(file -> file.setPostId(postId))
                    .forEach(file -> {
                        int result = fileMapper.insertFile(file);
                        if (result != 1) {
                            throw new IllegalArgumentException("Failed to insert file: " + file);
                        }
                    });
        } catch (IllegalArgumentException e){
            throw e;
        } 
        catch (RuntimeException e) {
            log.error("Update Files ERROR! {}", currentFiles);
            throw new IllegalStateException("Invalid Files for Post" + currentFiles + e.getMessage());
        }
    }
}