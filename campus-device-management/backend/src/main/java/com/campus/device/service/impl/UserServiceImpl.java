package com.campus.device.service.impl;

import com.campus.device.dao.UserMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.User;
import com.campus.device.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<User> listUsers(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectAll();
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "User not found: " + id);
        }
        return user;
    }

    @Override
    @Transactional
    public void addUser(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "Username already exists: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        user.setUpdatedAt(new Date());
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        int rows = userMapper.update(user);
        if (rows == 0) {
            throw new BusinessException(404, "User not found: " + user.getId());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        int rows = userMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "User not found: " + id);
        }
    }
}
