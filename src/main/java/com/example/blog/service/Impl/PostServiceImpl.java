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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@Log4j2
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userProfileMapper;

    @Autowired
    private FileMapper fileMapper;

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
        addAdditional(postDTO.getId(), postDTO.getFiles(), FileDTO::setPostId, fileMapper::insertFile);
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
            Long id = postDTO.getId();
            updateAdditional(id, fileMapper.findByPostId(id), postDTO.getFiles(), 
            FileDTO::setPostId, FileDTO::getId, fileMapper::insertFile, fileMapper::deleteFile);
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
        addAdditional(postDTO.getId(), postDTO.getFiles(), FileDTO::setPostId, fileMapper::insertFile);
    }

    public void updateFiles(PostDTO postDTO){
        updateAdditional(postDTO.getId(), fileMapper.findByPostId(postDTO.getId()), postDTO.getFiles(), 
            FileDTO::setPostId, FileDTO::getId, fileMapper::insertFile, fileMapper::deleteFile);
    }

    public <T> void addAdditional(Long postId, List<T> items,BiConsumer<T, Long> postIdSetter, Function<T, Integer> insertFunction) {
        try{
            // Assign the postId to each FileDTO and persist them
            items.stream()
                .peek(item -> postIdSetter.accept(item, postId))
                .forEach(item -> {
                        int result = insertFunction.apply(item);
                        if (result != 1) {
                            throw new IllegalArgumentException("Failed to insert item: " + item);
                        }
                    });
        } catch(IllegalArgumentException e){
            throw e;
        }
        catch (RuntimeException e){
            log.error("Attach Additional Items ERROR! {}", items);
            throw new IllegalStateException("Invalid Additional Items for Post" + items + e.getMessage());
        }
    }

    public <T, ID> void updateAdditional(Long postId, List<T> oldItems, List<T> newItems, 
            BiConsumer<T, Long> postIdSetter, 
            Function<T, ID> itemIdGetter,
            Function<T, Integer> insertFunction, 
            Function<ID, Integer> deleteFunction) {
        try {
            // Convert lists to sets for efficient lookup
            Set<T> oldSet = new HashSet<>(oldItems);
            Set<T> newSet = new HashSet<>(newItems);

            // * Delete removed items
            // Remove items that exist in DB but are no longer present in the updated DTO
            // (Comparison is based on equals/hashCode - value-based comparison)
            oldItems.stream()
                .filter(oldItem -> !newSet.contains(oldItem))  // Select oldItems that newItems don't have
                .forEach(oldItem -> {
                        int result = deleteFunction.apply(itemIdGetter.apply(oldItem));
                        if (result != 1) {
                            throw new IllegalArgumentException("Failed to delete item: " + oldItem);
                        }
                    }); 

            // * Insert newly added items
            // Add items that are present in the updated DTO but not yet stored in DB
            // Set postId and creation time before inserting
            newItems.stream()
                    .filter(newItem -> !oldSet.contains(newItem)) // Select newItems that oldItems don't have
                    .peek(newItem -> postIdSetter.accept(newItem, postId))
                    .forEach(newItem -> {
                        int result = insertFunction.apply(newItem);
                        if (result != 1) {
                            throw new IllegalArgumentException("Failed to insert item: " + newItem);
                        }
                    });
        } catch (IllegalArgumentException e){
            throw e;
        }
        catch (RuntimeException e) {
            log.error("Update Additional Items ERROR! {}", newItems);
            throw new IllegalStateException("Invalid Additional Items for Post" + newItems + e.getMessage());
        }
    }
}