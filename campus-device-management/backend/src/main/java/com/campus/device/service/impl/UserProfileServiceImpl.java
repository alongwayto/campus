package com.campus.device.service.impl;

import com.campus.device.dao.LoginRecordMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.dao.UserProfileMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.entity.LoginRecord;
import com.campus.device.model.entity.User;
import com.campus.device.model.entity.UserProfile;
import com.campus.device.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final LoginRecordMapper loginRecordMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfile getProfile(Long userId) {
        UserProfile profile = userProfileMapper.selectByUserId(userId);
        if (profile == null) {
            profile = UserProfile.builder()
                    .userId(userId)
                    .createdAt(new Date())
                    .build();
            userProfileMapper.insert(profile);
        }
        return profile;
    }

    @Override
    public void updateProfile(Long userId, UserProfile profile) {
        profile.setUserId(userId);
        UserProfile existing = userProfileMapper.selectByUserId(userId);
        if (existing == null) {
            profile.setCreatedAt(new Date());
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.update(profile);
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "当前密码错误");
        }
        User updateUser = User.builder()
                .id(userId)
                .password(passwordEncoder.encode(newPassword))
                .updatedAt(new Date())
                .build();
        userMapper.update(updateUser);
    }

    @Override
    public List<LoginRecord> getLoginRecords(Long userId, int limit) {
        return loginRecordMapper.selectByUserId(userId, limit);
    }
}
