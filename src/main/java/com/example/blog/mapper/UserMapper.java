package com.example.blog.mapper;

import com.example.blog.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {
    // return inserted rows
    public int insertUserProfile(UserDTO userDTO);

    public UserDTO findByUserIdAndPassword(@Param("userId") String userId,
                            @Param("password") String password);

    public int idCheck(@Param("userId") String userId);

    public UserDTO getUserProfile(@Param("id") Long id);

    public int updatePassword(@Param("id") Long id, 
                            @Param("password") String password);

    public int updateUserProfile(UserDTO userDTO);

    public int deleteUserProfile(@Param("id") Long id);
}