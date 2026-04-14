package com.campus.device.service;

import com.campus.device.model.entity.LoginRecord;
import com.campus.device.model.entity.UserProfile;

import java.util.List;

public interface UserProfileService {

    UserProfile getProfile(Long userId);

    void updateProfile(Long userId, UserProfile profile);

    void changePassword(Long userId, String oldPassword, String newPassword);

    List<LoginRecord> getLoginRecords(Long userId, int limit);
}
