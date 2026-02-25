package com.example.blog.dto.request.user;

import com.example.blog.dto.UserDTO;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequest {
    @NonNull
    private String userId;
    @NonNull
    private String password;
    @NonNull
    private String nickname;
}
