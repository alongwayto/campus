package com.campus.device.service;

import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.User;

public interface UserService {

    PageResult<User> listUsers(int pageNum, int pageSize);

    User getById(Long id);

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);
}
