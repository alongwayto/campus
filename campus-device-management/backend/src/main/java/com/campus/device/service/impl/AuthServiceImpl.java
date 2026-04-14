package com.campus.device.service.impl;

import com.campus.device.dao.RoleMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.dto.LoginRequest;
import com.campus.device.model.dto.LoginResponse;
import com.campus.device.model.entity.Role;
import com.campus.device.model.entity.User;
import com.campus.device.security.JwtUtils;
import com.campus.device.service.AuthService;
import com.campus.device.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final CaptchaService captchaService;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (StringUtils.hasText(request.getCaptchaCode()) && StringUtils.hasText(request.getSessionId())) {
            if (!captchaService.validateCaptcha(request.getSessionId(), request.getCaptchaCode())) {
                throw new BusinessException(400, "验证码错误");
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
            throw new BusinessException(401, "Invalid username or password");
        }
    }

    @Override
    public void logout(String token) {
        // Stateless JWT - client discards token; no server-side invalidation needed
    }
}
