package com.example.blog.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Keep this annotation available at runtime for AOP
@Target(ElementType.METHOD) // Can only be applied to methods
public @interface LoginCheck {
    public static enum UserType {
        USER, ADMIN
    }
    UserType type();
}