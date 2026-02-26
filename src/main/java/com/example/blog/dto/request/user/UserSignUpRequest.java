package com.example.blog.dto.request.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequest {
    @NotNull(message = "{short.user.id}")
    @Size(min=4, message= "{short.user.id}")
    @Size(max=16, message= "{long.user.id}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{invalid.user.id.format}")
    private String userId;

    @NotNull(message = "{short.password}")
    @Size(min=8, message= "{short.password}")
    @Size(max=20, message= "{long.password}")
    @Pattern(regexp="^[a-zA-Z0-9!@#$%^&*]+$", message="{invalid.password.format}")
    private String password;

    @NotNull(message="{invalid.nickname.format}")
    @Size(max=16, message="{long.nickname}")
    @Pattern(regexp="^[a-zA-Z0-9_]+$", message="{invalid.nickname.format}")
    private String nickname;
}
