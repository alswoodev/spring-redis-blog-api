package com.example.blog.aop;

import com.example.blog.exception.UnauthorizedException;
import com.example.blog.utils.SessionUtil;

import jakarta.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class LoginCheckAspect {
    // Around Advice applies to methods matching the pointcut condition (@LoginCheck)
    // and binds relevant objects to the advice parameters:
    // - joinPoint: the context of the intercepted method (method info, args, target object)
    // - loginCheck: the actual @LoginCheck annotation instance attached to the method
    @Around("@annotation(com.example.blog.aop.LoginCheck) && @annotation(loginCheck)")
    public Object adminLoginCheck(ProceedingJoinPoint proceedingJoinPoint, LoginCheck loginCheck) throws Throwable {
        // 1. Spring MVC binds each HTTP request to the current thread. We can access this thread-bound request using RequestContextHolder.
        // 2. Get RequestAttributes object from RequestContextHolder
        // 3. Casting RequestAttributes into ServletRequestAttributes
        ServletRequestAttributes requestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        // 1. Get HttpServletRequest from ServletRequestAttributes
        // 2. Get HttpSession from HttpServletRequest
        HttpSession session = requestAttribute.getRequest().getSession();

        Long id = null;
        int idIndex = 0;


        String userType = loginCheck.type().toString();
        switch (userType) {
            case "ADMIN": {
                id = SessionUtil.getLoginAdminId(session);
                break;
            }
            case "USER": {
                id = SessionUtil.getLoginMemberId(session);
                break;
            }
        }
        if (id == null) {
            log.debug(proceedingJoinPoint.toString()+ "accountName :" + id);
            // If session ID is null, skip controller and return 401 Unauthorized
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Object[] modifiedArgs = proceedingJoinPoint.getArgs();

        // First argument in controller is null because it's not injected by Spring.
        // so args[idIndex] is initially null.
        if(modifiedArgs!=null) modifiedArgs[idIndex] = id;

        return proceedingJoinPoint.proceed(modifiedArgs);
    }

}