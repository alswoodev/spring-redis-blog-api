package com.example.blog.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.blog.exception.InvalidParameterException;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingThrownException {
    
    @Pointcut("execution(* com.example..service..*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object loggingException(ProceedingJoinPoint joinPoint) throws Throwable{

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for(Object arg : args){
            sb.append(arg).append(", ");
        }

        try {
            return joinPoint.proceed();
        } catch (InvalidParameterException ex) {
            String exceptionMessage = ex.getMessage();
            log.warn("{} in [{}] method: {} with parameters: {}. ExceptionKey: {}",
                            ex.getClass().getName(), className, methodName, sb.toString().trim(), exceptionMessage);

            throw ex;
        } catch (RuntimeException ex){
            String exceptionMessage = ex.getMessage();
            log.error("{} in [{}] method: {} with parameters: {}. ExceptionKey: {}",
                            ex.getClass().getName(), className, methodName, sb.toString().trim(), exceptionMessage);
            throw ex;
        }
    }
}
