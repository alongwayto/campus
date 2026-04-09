package com.campus.device.service;

import com.campus.device.model.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> listAll();

    Role getById(Long id);

    void add(Role role);

    void update(Role role);

    void delete(Long id);
}
