package com.campus.device.service.impl;

import com.campus.device.dao.LoginRecordMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.dao.UserProfileMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.entity.User;
import com.campus.device.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final LoginRecordMapper loginRecordMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("roleId", user.getRoleId());
        result.put("enabled", user.getEnabled());
        result.put("createdAt", user.getCreatedAt());

        // Get profile details
        Map<String, Object> profile = userProfileMapper.selectByUserId(userId);
        if (profile != null) {
            result.put("avatarUrl", profile.get("avatar_url"));
            result.put("department", profile.get("department"));
            result.put("position", profile.get("position"));
            result.put("lastLoginTime", profile.get("last_login_time"));
            result.put("lastLoginIp", profile.get("last_login_ip"));
        }

        return result;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, Map<String, String> profileData) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // Update user basic info
        if (profileData.containsKey("realName")) {
            user.setRealName(profileData.get("realName"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail(profileData.get("email"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone(profileData.get("phone"));
        }
        user.setUpdatedAt(new Date());
        userMapper.update(user);

        // Update profile details
        String department = profileData.getOrDefault("department", "");
        String position = profileData.getOrDefault("position", "");

        Map<String, Object> existingProfile = userProfileMapper.selectByUserId(userId);
        if (existingProfile != null) {
            userProfileMapper.updateProfile(userId, department, position);
        } else {
            userProfileMapper.insert(userId, null, department, position, null, null, null);
        }

        log.info("User profile updated for userId: {}", userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // Need to get the password - selectById might not include it
        // Use selectByUsername to get full user with password
        User fullUser = userMapper.selectByUsername(user.getUsername());
        if (fullUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, fullUser.getPassword())) {
            throw new BusinessException(400, "当前密码错误");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException(400, "新密码不能少于6位");
        }

        // Update password
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, encodedPassword);

        log.info("Password changed for userId: {}", userId);
    }

    @Override
    public List<Map<String, Object>> getLoginRecords(Long userId, int limit) {
        return loginRecordMapper.selectByUserId(userId, limit > 0 ? limit : 10);
    }
}
