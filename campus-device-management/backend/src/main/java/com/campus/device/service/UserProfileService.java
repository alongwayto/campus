package com.campus.device.service;

import java.util.List;
import java.util.Map;

public interface UserProfileService {

    Map<String, Object> getUserProfile(Long userId);

    void updateProfile(Long userId, Map<String, String> profileData);

    void changePassword(Long userId, String oldPassword, String newPassword);

    List<Map<String, Object>> getLoginRecords(Long userId, int limit);
}
