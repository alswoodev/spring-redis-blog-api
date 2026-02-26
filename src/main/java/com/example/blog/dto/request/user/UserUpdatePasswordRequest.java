package com.example.blog.dto.request.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdatePasswordRequest {
    @NotNull(message = "{short.password}")
    @Size(min=8, message= "{short.password}")
    @Size(max=20, message= "{long.password}")
    @Pattern(regexp="^[a-zA-Z0-9!@#$%^&*]+$", message="{invalid.password.format}")
    private String beforePassword;
    
    @NotNull(message = "{short.password}")
    @Size(min=8, message= "{short.password}")
    @Size(max=20, message= "{long.password}")
    @Pattern(regexp="^[a-zA-Z0-9!@#$%^&*]+$", message="{invalid.password.format}")
    private String afterPassword;
}