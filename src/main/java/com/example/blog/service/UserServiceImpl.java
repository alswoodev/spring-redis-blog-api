package com.example.blog.service;

import com.example.blog.dto.UserDTO;
import com.example.blog.exception.DuplicateIdException;
import com.example.blog.mapper.UserProfileMapper;
import com.example.blog.utils.SHA256Util;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    public UserServiceImpl(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public UserDTO getUserInfo(String userId) {
        return userProfileMapper.getUserProfile(userId);
    }

    @Override
    public void register(UserDTO userDTO) {
        // Duplicate check
        boolean duplIdResult = isDuplicatedId(userDTO.getUserId());
        if (duplIdResult) {
            throw new DuplicateIdException("중복된 아이디입니다.");
        }

        userDTO.setCreateTime(new Date());
        // Hashing password
        userDTO.setPassword(SHA256Util.encryptSHA256(userDTO.getPassword()));

        // Insert user
        int insertCount = userProfileMapper.register(userDTO);

        // If everything is correct, count must be 1
        if (insertCount != 1) {
            log.error("insertMember ERROR! {}", userDTO);
            throw new RuntimeException(
                    "insertUser ERROR! 회원가입 메서드를 확인해주세요\n" + "Params : " + userDTO);
        }
    }

    @Override
    public UserDTO login(String id, String password) {
        // Hash the input password
        String cryptoPassword = SHA256Util.encryptSHA256(password);

        UserDTO memberInfo = userProfileMapper.findByIdAndPassword(id, cryptoPassword);
        return memberInfo;
    }

    @Override
    public boolean isDuplicatedId(String id) {
        return userProfileMapper.idCheck(id) == 1;
    }

    @Override
    public void updatePassword(String id, String beforePassword, String afterPassword) {
        // Hash the input before password
        String cryptoPassword = SHA256Util.encryptSHA256(beforePassword);
        UserDTO memberInfo = userProfileMapper.findByIdAndPassword(id, cryptoPassword);

        // Find the user if the previous password is correct
        if (memberInfo != null) {
            memberInfo.setPassword(SHA256Util.encryptSHA256(afterPassword));
            int insertCount = userProfileMapper.updatePassword(memberInfo);

            // If fail to insert
            if (insertCount != 1) {
                log.error("insertMember ERROR! {}", memberInfo);
                throw new RuntimeException(
                        "insertUser ERROR! \n" + "Params : " + memberInfo);
            }
        } else {
            log.error("updatePasswrod ERROR! {}", memberInfo);
            throw new IllegalArgumentException("updatePasswrod ERROR! 비밀번호 변경 메서드를 확인해주세요\n" + "Params : " + memberInfo);
        }
    }

    @Override
    public void deleteId(String id, String passWord) {
        // Hash the input password
        String cryptoPassword = SHA256Util.encryptSHA256(passWord);
        UserDTO memberInfo = userProfileMapper.findByIdAndPassword(id, cryptoPassword);

        // Find the user if the password is correct
        if (memberInfo != null) {
            userProfileMapper.deleteUserProfile(memberInfo.getUserId());
        } else {
            log.error("deleteId ERROR! {}", memberInfo);
            throw new RuntimeException("deleteId ERROR! id 삭제 메서드를 확인해주세요\n" + "Params : " + memberInfo);
        }
    }
}