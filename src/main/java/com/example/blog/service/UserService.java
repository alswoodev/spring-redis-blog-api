package com.example.blog.service;

import com.example.blog.dto.UserDTO;

public interface UserService {

    void register(UserDTO userProfile);

    UserDTO login(String userId, String password);

    boolean isDuplicatedId(String userId);

    UserDTO getUserInfo(Long id);

    void updatePassword(Long id, String beforePassword, String afterPassword);

    void deleteId(Long id, String password);
}