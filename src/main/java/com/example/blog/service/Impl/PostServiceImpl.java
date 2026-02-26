package com.example.blog.service.Impl;

import com.example.blog.code.PostCode;
import com.example.blog.code.UserCode;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.FileDTO;
import com.example.blog.dto.PostDTO;
import com.example.blog.dto.TagDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.exception.InvalidParameterException;
import com.example.blog.exception.UnauthorizedException;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.FileMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.PostService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@Slf4j
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

    @Autowired
    private PostServiceCache postServiceCache;

    @Override
    @Transactional
    public void register(Long userId, PostDTO postDTO) {
        // Validate userId
        UserDTO memberInfo = userProfileMapper.getUserProfile(userId);

        if (memberInfo != null) {
            postDTO.setUserId(memberInfo.getId());
            postDTO.setPreview(makePreview(postDTO.getContents()));
            PostDTO.hasNullData(postDTO);
            int count = postMapper.insertPost(postDTO);
            if(count != 1) throw new RuntimeException("게시글 등록 중 오류가 발생했습니다.");
        } else {
            log.error("Failed to load user {}", userId);
            throw new InvalidParameterException("id", UserCode.USER_NOT_FOUND);
        }

        attachFiles(postDTO); // Attach files 
        attachTags(postDTO); // Attach tags
    }

    // This method doesn't have detail information (e.g. files)
    @Override
    public List<PostDTO> findMyPosts(Long userId) {
        if(userId != null && userId != 0) return postMapper.findAllByUserId(userId);
        else{
            log.error("Failed to load user {}", userId);
            throw new InvalidParameterException("id", UserCode.USER_NOT_FOUND);
        }
    }


    // To improve caching efficiency, split post information into writer-frequent (static) and reader-frequent (dynamic) parts.
    // Only the static part is cached, since the dynamic part changes frequently.
    // The dynamic part is retrieved separately after fetching the static part.
    @Override 
    public PostDTO getPostDetail(Long id){
        if(id != null && id != 0) {
            PostDTO postDTO = postServiceCache.getStaticPost(id);
            postDTO.setComments(commentMapper.findByPostId(id));
            return postDTO;
        }
        else{
            log.error("get Post ERROR! Invalid postId {}", id);
            throw new InvalidParameterException("id", PostCode.POST_NOT_FOUND);
        }
    }

    @CacheEvict(value = "postCache", key = "'getPost' + #postDTO.id")
    @Override
    @Transactional
    public void updatePost(Long userId, PostDTO postDTO) {
        if (!Objects.equals(userId, postDTO.getUserId())) throw new UnauthorizedException("해당 게시글을 수정할 수 없는 유저");

        if (postDTO != null && postDTO.getId() != 0 && postDTO.getUserId() != 0) {
            PostDTO.hasNullData(postDTO);
            postDTO.setPreview(makePreview(postDTO.getContents()));
            postDTO.setUpdateTime(new Date());
            int count = postMapper.updatePost(postDTO);
            if (count != 1) throw new RuntimeException("게시글 수정중 오류가 발생했습니다.");

            updateFiles(postDTO); // Update files
            updateTags(postDTO); // Update tags
        } else {
            log.error("get Post ERROR! Invalid postId {}", postDTO.getId());
            throw new InvalidParameterException("id", PostCode.POST_NOT_FOUND);
        }
    }

    @CacheEvict(value = "postCache", key = "'getPost' + #id")
    @Override
    public void deletePost(Long userId, Long id) {
        PostDTO postDTO = postMapper.getPost(id);
        if(postDTO == null) {
            log.error("get Post ERROR! Invalid postId {}", id);
            throw new InvalidParameterException("id", PostCode.POST_NOT_FOUND);
        }
        if (!Objects.equals(userId, postDTO.getUserId())) throw new UnauthorizedException("해당 게시글을 삭제할 수 없는 유저");
        
        int count = postMapper.deletePost(id);
        if(count != 1) throw new RuntimeException("게시글 삭제중 오류가 발생했습니다.");
        // Files will be deleted automatically via cascade
    }

    @Override
    @Transactional
    public void registerComment(CommentDTO commentDTO){
        commentMapper.insertComment(commentDTO);
        postMapper.increaseCount(commentDTO.getPostId());
    }

    @Override
    public CommentDTO getCommentDetail(Long commentId){
        if(commentId != null && !commentId.equals(0L)) {
            CommentDTO commentDTO = commentMapper.getById(commentId);
            if(commentDTO != null) return commentDTO;
            else return null;
        }
        else{
            log.error("get Comment ERROR! No commentId {}", commentId);
            throw new InvalidParameterException("id", PostCode.COMMENT_NOT_FOUND);
        }
    }

    @Override
    public void updateComment(Long userId, CommentDTO commentDTO){
        if(commentDTO.getId() == null || commentDTO.getId().equals(0L)) throw new InvalidParameterException("id", PostCode.COMMENT_NOT_FOUND);
        CommentDTO existingComment = commentMapper.getById(commentDTO.getId());
        if(existingComment == null) throw new InvalidParameterException("id", PostCode.COMMENT_NOT_FOUND);
        if (!Objects.equals(userId, existingComment.getUserId())) throw new UnauthorizedException("해당 댓글을 수정할 수 없는 유저");
        commentDTO.setUpdateTime(new Date());
        commentMapper.updateComment(commentDTO);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId){
        if(commentId == null || commentId.equals(0L)) throw new InvalidParameterException("id", PostCode.COMMENT_NOT_FOUND);
        CommentDTO existingComment = commentMapper.getById(commentId);
        if(existingComment == null) throw new InvalidParameterException("id", PostCode.COMMENT_NOT_FOUND);
        if (!Objects.equals(userId, existingComment.getUserId())) throw new UnauthorizedException("해당 댓글을 삭제할 수 없는 유저");
        commentMapper.deleteComment(commentId);
        int updated = postMapper.decreaseCount(existingComment.getPostId());
        if(updated != 1) throw new InvalidParameterException("id", PostCode.POST_NOT_FOUND);
    }

    public void attachFiles(PostDTO postDTO){
        if(postDTO.getFiles() == null || postDTO.getFiles().isEmpty()) return; // No files to attach
        addAdditional(postDTO.getId(), postDTO.getFiles(), FileDTO::setPostId, fileMapper::insertFile);
    }

    @CacheEvict(value = "postCache", key = "'getPost' + #postDTO.getId()")
    public void updateFiles(PostDTO postDTO){
        if(postDTO.getFiles() == null) return; // If files field is null, we assume that the files don't need to be updated.
        updateAdditional(postDTO.getId(), fileMapper.findByPostId(postDTO.getId()), postDTO.getFiles(), 
            FileDTO::setPostId, FileDTO::getId, fileMapper::insertFile, fileMapper::deleteFile);
    }

    public void addTag(TagDTO tagDTO){
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

    @CacheEvict(value = "postCache", key = "'getPost' + #postDTO.getId()")
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
        if(postId == null || postId.equals(0L)) throw new InvalidParameterException("postId", PostCode.ADDITIONAL_NO_POST_ID);
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
        if(postId == null || postId.equals(0L)) throw new InvalidParameterException("postId", PostCode.ADDITIONAL_NO_POST_ID);
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

    private String makePreview(String content) {
        if (content == null) return null;
        return content.length() <= 200 ? content : content.substring(0, 190);
    }
}