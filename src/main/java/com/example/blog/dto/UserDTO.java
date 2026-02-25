package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    public enum Status {
        DEFAULT, ADMIN, DELETED
    }
    private Long id;
    private String userId;
    private String password;
    private String nickname;
    @Builder.Default
    private boolean isAdmin = false;
    @Builder.Default
    private Status status = Status.DEFAULT;
    private Date createTime;
    private Date updateTime;
}