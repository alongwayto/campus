package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.service.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "User Profile")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @ApiOperation("Get user profile")
    @GetMapping("/profile/{userId}")
    public Result<Map<String, Object>> getProfile(@PathVariable Long userId) {
        return Result.success(userProfileService.getUserProfile(userId));
    }

    @ApiOperation("Update user profile")
    @Log(operation = "Update Profile")
    @PutMapping("/profile/{userId}")
    public Result<Void> updateProfile(@PathVariable Long userId, @RequestBody Map<String, String> profileData) {
        userProfileService.updateProfile(userId, profileData);
        return Result.success();
    }

    @ApiOperation("Change password")
    @Log(operation = "Change Password")
    @PostMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(request.get("userId"));
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        userProfileService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @ApiOperation("Get login records")
    @GetMapping("/login-records/{userId}")
    public Result<List<Map<String, Object>>> getLoginRecords(@PathVariable Long userId,
                                                              @RequestParam(defaultValue = "10") int limit) {
        return Result.success(userProfileService.getLoginRecords(userId, limit));
    }
}
