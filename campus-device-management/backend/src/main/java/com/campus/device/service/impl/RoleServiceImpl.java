package com.campus.device.service.impl;

import com.campus.device.dao.RoleMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.entity.Role;
import com.campus.device.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<Role> listAll() {
        return roleMapper.selectAll();
    }

    @Override
    public Role getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "Role not found: " + id);
        }
        return role;
    }

    @Override
    @Transactional
    public void add(Role role) {
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void update(Role role) {
        int rows = roleMapper.update(role);
        if (rows == 0) {
            throw new BusinessException(404, "Role not found: " + role.getId());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int rows = roleMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "Role not found: " + id);
        }
    }
}
