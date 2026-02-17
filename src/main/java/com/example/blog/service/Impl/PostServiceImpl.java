package com.example.blog.service.Impl;

import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.TagDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.FileMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.PostService;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public void register(Long userId, PostDTO postDTO) {
        // Validate userId
        UserDTO memberInfo = userProfileMapper.getUserProfile(userId);

        if (memberInfo != null) {
            postDTO.setUserId(memberInfo.getId());
            if(PostDTO.hasNullData(postDTO) == true) throw new IllegalArgumentException("제목, 내용은 필수 입력값입니다.");
            int count = postMapper.insertPost(postDTO);
            if(count != 1) throw new IllegalArgumentException("Failed to register post : " + postDTO);
        } else {
            log.error("Failed to load user {}", userId);
            throw new IllegalArgumentException("Failed to load user\n" + "Params : " + userId+ postDTO);
        }

        attachFiles(postDTO); // Attach files 
        attachTags(postDTO); // Attach tags   
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
        if(id != null && id != 0) {
            PostDTO postDTO = postMapper.getPost(id);
            if(postDTO != null) {
                postDTO.setFiles(fileMapper.findByPostId(id));
                postDTO.setTags(tagMapper.findByPostId(id));
                postDTO.setComments(commentMapper.findByPostId(id));
                return postDTO;
            }
            else {
                log.error("get Post ERROR! Invalid postId {}", id);
                throw new IllegalArgumentException("get Post ERROR! Invalid postId\n" + "Param : " + id);
            }
        }
        else{
            log.error("get Post ERROR! No postId {}");
            throw new IllegalArgumentException("get Post ERROR! No postId");
        }
    }

    @Override
    @Transactional
    public void updatePost(PostDTO postDTO) {
        if (postDTO != null && postDTO.getId() != 0 && postDTO.getUserId() != 0) {
            if(PostDTO.hasNullData(postDTO) == true) throw new IllegalArgumentException("제목, 내용은 필수 입력값입니다.");
            postDTO.setUpdateTime(new Date());
            int count = postMapper.updatePost(postDTO);
            if (count != 1) throw new IllegalArgumentException("Failed to update post : " + postDTO);

            updateFiles(postDTO); // Update files
            updateTags(postDTO); // Update tags
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

    @Override
    public void registerComment(CommentDTO commentDTO){
        if(commentDTO.getContents() == null || commentDTO.getContents().trim().isEmpty()) throw new IllegalArgumentException("빈 댓글은 등록할 수 없습니다.");
        if(commentDTO.getContents().length() > 250) throw new IllegalArgumentException("댓글을 250자 이상 작성할 수 없습니다.");
        commentMapper.insertComment(commentDTO);
    }

    @Override
    public CommentDTO getCommentDetail(Long commentId){
        if(commentId != null && !commentId.equals(0L)) {
            CommentDTO commentDTO = commentMapper.getById(commentId);
            if(commentDTO != null) return commentDTO;
            else return null;
        }
        else{
            log.error("get Comment ERROR! No commentId {}");
            throw new IllegalArgumentException("get Comment ERROR! No commentId");
        }
    }

    @Override
    public void updateComment(CommentDTO commentDTO){
        if(commentDTO.getId() == null || commentDTO.getId().equals(0L)) throw new IllegalArgumentException("Invalid commentId");
        if(commentDTO.getContents() == null || commentDTO.getContents().trim().isEmpty()) throw new IllegalArgumentException("빈 댓글은 수정할 수 없습니다.");
        if(commentDTO.getContents().length() > 250) throw new IllegalArgumentException("댓글을 250자 이상 작성할 수 없습니다.");
        commentDTO.setUpdateTime(new Date());
        commentMapper.updateComment(commentDTO);
    }

    @Override
    public void deleteComment(Long commentId){
        if(commentId == null || commentId.equals(0L)) throw new IllegalArgumentException("Invalid commentId");
        commentMapper.deleteComment(commentId);
    }



    public void attachFiles(PostDTO postDTO){
        if(postDTO.getFiles() == null || postDTO.getFiles().isEmpty()) return; // No files to attach
        addAdditional(postDTO.getId(), postDTO.getFiles(), FileDTO::setPostId, fileMapper::insertFile);
    }

    public void updateFiles(PostDTO postDTO){
        if(postDTO.getFiles() == null) return; // If files field is null, we assume that the files don't need to be updated.
        updateAdditional(postDTO.getId(), fileMapper.findByPostId(postDTO.getId()), postDTO.getFiles(), 
            FileDTO::setPostId, FileDTO::getId, fileMapper::insertFile, fileMapper::deleteFile);
    }

    public void addTag(TagDTO tagDTO){
        if(tagDTO.getName() == null || tagDTO.getName().trim().isEmpty()) throw new IllegalArgumentException("Tag name should not be empty");
        if(tagDTO.getName().length() < 2) throw new IllegalArgumentException("Tag name should be at least 2 characters");
        if(tagDTO.getName().length() > 40) throw new IllegalArgumentException("Tag name should be less than 40 characters");
        if(tagDTO.getName().contains(" ")) throw new IllegalArgumentException("Tag name should not contain spaces");
        // Check if the tag already exists in the tags table to prevent duplicates
        TagDTO existingTag = tagMapper.findByName(tagDTO.getName());
        if(existingTag == null) {
            tagMapper.insertTag(tagDTO);
        } else {
            tagDTO.setId(existingTag.getId());
            tagDTO.setCreateTime(existingTag.getCreateTime());
        }
    }

    public void attachTags(PostDTO postDTO){
        if(postDTO.getTags() == null || postDTO.getTags().isEmpty()) return;
        postDTO.getTags().forEach(tag -> addTag(tag));
        addAdditional(postDTO.getId(), postDTO.getTags(), TagDTO::setPostId, tagMapper::insertPostTag);
    }

    public void updateTags(PostDTO postDTO){
        if(postDTO.getTags() == null) return; // If tags field is null, we assume that the tags don't need to be updated.
        // Update tags table (N:M)
        Long id = postDTO.getId();
        List<TagDTO> oldTags = tagMapper.findByPostId(id);
        List<TagDTO> newTags = postDTO.getTags();

        // Ensure all new tags exist in the tags table
        newTags.forEach(tag -> addTag(tag));

        updateAdditional(id, oldTags, newTags, 
            TagDTO::setPostId, TagDTO::getId, tagMapper::insertPostTag, tagMapper::deleteTag);
    }
    // ----------------------------------------------------------
    // Descrpition : If you want to add additional information (e.g. files, tags), use the below methods to avoid code duplication
    // ! WARNING ! N:M ISSUE
    // * When what you want to add is N:M relationship with post (e.g. tags), make sure the DTO of the entity has postId field (even if it's not FK in table)
    // * make sure to insert data into the main table first, then add data into the relation table
    // ----------------------------------------------------------
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