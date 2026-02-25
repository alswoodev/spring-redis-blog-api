package com.example.blog.dto.request.user;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class UserSignInRequest {
    @NonNull
    private String userId;
    @NonNull
    private String password;
}