package com.example.blog.dto.request.user;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class UserDeleteId {
    @NonNull
    private String password;
}