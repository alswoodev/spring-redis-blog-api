package com.example.blog.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class UserDTO {
    public enum Status {
        DEFAULT, ADMIN, DELETED
    }
    private Long id;
    private String userId;
    private String password;
    private String nickname;
    private boolean isAdmin = false;
    private Status status = Status.DEFAULT;
    private Date createTime;
    private Date updateTime;

    public UserDTO(){
    }

    public UserDTO(String id, String password, String name, String phone, String address, Status status, Date createTime, Date updateTime, boolean isAdmin) {
        this.userId = id;
        this.password = password;
        this.nickname = name;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isAdmin = isAdmin;
    }

    public static boolean hasNullDataBeforeSignup(UserDTO userDTO) {
        return userDTO.getUserId() == null || userDTO.getPassword() == null
                || userDTO.getNickname() == null;
    }
}