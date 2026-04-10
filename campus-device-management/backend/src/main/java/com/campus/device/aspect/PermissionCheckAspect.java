package com.campus.device.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PermissionCheckAspect {

    @Around("@annotation(com.campus.device.aspect.RequireRole)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("用户未认证");
        }
        log.debug("Permission check passed for user: {}", auth.getName());
        return joinPoint.proceed();
    }
}
