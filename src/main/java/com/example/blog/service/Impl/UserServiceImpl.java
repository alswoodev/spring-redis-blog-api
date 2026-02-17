package com.example.blog.service.Impl;

import com.example.blog.dto.UserDTO;
import com.example.blog.exception.InvalidParameterException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.UserService;
import com.example.blog.utils.SHA256Util;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userProfileMapper;

    public UserServiceImpl(UserMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public void register(UserDTO userDTO) {
        // Duplicate check
        boolean duplIdResult = isDuplicatedId(userDTO.getUserId());
        if (duplIdResult) {
            throw new InvalidParameterException("userId", com.example.blog.code.UserCode.USER_ALREADY_EXISTS);
        }

        // Hashing password
        userDTO.setPassword(SHA256Util.encryptSHA256(userDTO.getPassword()));

        // Insert user
        int insertCount = userProfileMapper.insertUserProfile(userDTO);

        // If everything is correct, count must be 1
        if (insertCount != 1) {
            log.error("insertMember ERROR! {}", userDTO);
            throw new RuntimeException(
                    "insertUser ERROR! 회원가입 메서드를 확인해주세요\n" + "Params : " + userDTO);
        }
    }

    @Override
    public boolean isDuplicatedId(String userId) {
        return userProfileMapper.idCheck(userId) > 0;
    }

    @Override
    public UserDTO login(String userId, String password) {
        // Hash the input password
        String cryptoPassword = SHA256Util.encryptSHA256(password);

        UserDTO memberInfo = userProfileMapper.findByUserIdAndPassword(userId, cryptoPassword);
        if(memberInfo == null) throw new InvalidParameterException("password", com.example.blog.code.UserCode.INCORRECT_PASSWORD);
        return memberInfo;
    }

    @Override
    public UserDTO getUserInfo(Long id) {
        UserDTO memberInfo = userProfileMapper.getUserProfile(id);
        if(memberInfo == null) throw new InvalidParameterException("id", com.example.blog.code.UserCode.USER_NOT_FOUND);
        return memberInfo;
    }

    @Override
    public void updatePassword(Long id, String beforePassword, String afterPassword) {
        // Hash the input before password
        String cryptoPassword = SHA256Util.encryptSHA256(beforePassword);
        UserDTO memberInfo = userProfileMapper.getUserProfile(id);
        if(memberInfo == null) throw new InvalidParameterException("id", com.example.blog.code.UserCode.USER_NOT_FOUND);

        // Find the user if the previous password is correct
        if (Objects.equals(memberInfo.getPassword(), cryptoPassword)) {
            int updateCount = userProfileMapper.updatePassword(id, SHA256Util.encryptSHA256(afterPassword));

            // If fail to insert
            if (updateCount != 1) {
                log.error("updateUser ERROR! {}", memberInfo);
                throw new RuntimeException(
                        "updateUser ERROR! \n" + "Params : " + memberInfo);
            }
        } else {
            throw new InvalidParameterException("password", com.example.blog.code.UserCode.INCORRECT_PASSWORD);
        }
    }

    @Override
    public void deleteId(Long id, String password) {
        // Hash the input password
        String cryptoPassword = SHA256Util.encryptSHA256(password);
        UserDTO memberInfo = userProfileMapper.getUserProfile(id);
        if (memberInfo == null) throw new InvalidParameterException("id", com.example.blog.code.UserCode.USER_NOT_FOUND);

        // Find the user if the password is correct
        if (Objects.equals(memberInfo.getPassword(), cryptoPassword)) {
            int count = userProfileMapper.deleteUserProfile(id);
            if (count != 1) {
                log.error("deleteUser ERROR! {}", memberInfo);
                throw new RuntimeException(
                        "deleteUser ERROR! \n" + "Params : " + memberInfo);
            }
        } else {
            throw new InvalidParameterException("password", com.example.blog.code.UserCode.INCORRECT_PASSWORD);
        }
    }
}