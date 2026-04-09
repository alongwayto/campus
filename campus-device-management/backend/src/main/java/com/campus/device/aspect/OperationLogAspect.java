package com.campus.device.aspect;

import com.campus.device.dao.LogMapper;
import com.campus.device.model.entity.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final LogMapper logMapper;

    @Around("@annotation(com.campus.device.aspect.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        String username = "anonymous";
        Long userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            username = authentication.getName();
        }

        String requestUrl = "";
        String ip = "";
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestUrl = request.getRequestURI();
            ip = getClientIp(request);
        }

        String operation = logAnnotation.operation().isEmpty() ? logAnnotation.value() : logAnnotation.operation();
        String methodName = point.getTarget().getClass().getName() + "." + method.getName();
        String params = sanitizeParams(Arrays.toString(point.getArgs()));

        Object result = null;
        String resultStr = "success";
        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            resultStr = "error: " + e.getMessage();
            throw e;
        } finally {
            OperationLog operationLog = OperationLog.builder()
                    .userId(userId)
                    .username(username)
                    .operation(operation)
                    .method(methodName)
                    .requestUrl(requestUrl)
                    .requestParam(truncate(params, 500))
                    .result(resultStr)
                    .ip(ip)
                    .createdAt(new Date())
                    .build();
            try {
                logMapper.insert(operationLog);
            } catch (Exception ex) {
                log.error("Failed to save operation log", ex);
            }
        }
    }

    private String sanitizeParams(String params) {
        if (params == null) return null;
        // Redact password fields
        return params.replaceAll("(?i)(password|passwd|secret|token)=[^,\\]]+", "$1=***");
    }

    private String getClientIp(HttpServletRequest request) {        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
