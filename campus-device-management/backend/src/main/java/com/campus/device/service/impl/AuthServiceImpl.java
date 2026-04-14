package com.campus.device.service.impl;

import com.campus.device.controller.CaptchaController;
import com.campus.device.dao.RoleMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.dto.LoginRequest;
import com.campus.device.model.dto.LoginResponse;
import com.campus.device.model.entity.Role;
import com.campus.device.model.entity.User;
import com.campus.device.security.JwtUtils;
import com.campus.device.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        // Validate captcha if provided
        if (request.getCaptchaCode() != null && request.getSessionId() != null) {
            boolean valid = CaptchaController.validateCaptcha(request.getSessionId(), request.getCaptchaCode());
            if (!valid) {
                throw new BusinessException(400, "验证码错误或已过期");
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String username = authentication.getName();
            User user = userMapper.selectByUsername(username);
            String roleName = "ROLE_USER";
            if (user != null && user.getRoleId() != null) {
                Role role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    roleName = role.getName();
                }
            }
            String token = jwtUtils.generateToken(username, roleName);
            return LoginResponse.builder()
                    .token(token)
                    .username(username)
                    .realName(user != null ? user.getRealName() : username)
                    .role(roleName)
                    .userId(user != null ? user.getId() : null)
                    .build();
        } catch (AuthenticationException e) {
            throw new BusinessException(401, "用户名或密码错误");
        }
    }

    @Override
    public void logout(String token) {
        // Stateless JWT - client discards token; no server-side invalidation needed
    }
}
