package com.campus.device.controller;

import com.campus.device.dao.UserMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.exception.Result;
import com.campus.device.model.dto.PasswordChangeRequest;
import com.campus.device.model.entity.LoginRecord;
import com.campus.device.model.entity.User;
import com.campus.device.model.entity.UserProfile;
import com.campus.device.service.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "User Profile")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserMapper userMapper;

    @ApiOperation("Get current user profile")
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile() {
        User user = getCurrentUser();
        UserProfile profile = userProfileService.getProfile(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("roleId", user.getRoleId());
        data.put("avatarUrl", profile.getAvatarUrl());
        data.put("department", profile.getDepartment());
        data.put("position", profile.getPosition());
        data.put("lastLoginTime", profile.getLastLoginTime());
        data.put("lastLoginIp", profile.getLastLoginIp());
        return Result.success(data);
    }

    @ApiOperation("Update current user profile")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserProfile profile) {
        User user = getCurrentUser();
        userProfileService.updateProfile(user.getId(), profile);
        return Result.success();
    }

    @ApiOperation("Change password")
    @PostMapping("/password")
    public Result<Void> changePassword(@Validated @RequestBody PasswordChangeRequest request) {
        User user = getCurrentUser();
        userProfileService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @ApiOperation("Get login records")
    @GetMapping("/login-records")
    public Result<List<LoginRecord>> getLoginRecords(
            @RequestParam(defaultValue = "10") int limit) {
        User user = getCurrentUser();
        List<LoginRecord> records = userProfileService.getLoginRecords(user.getId(), limit);
        return Result.success(records);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }
}
