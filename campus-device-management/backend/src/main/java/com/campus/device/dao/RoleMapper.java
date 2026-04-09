package com.campus.device.dao;

import com.campus.device.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> selectAll();

    Role selectById(@Param("id") Long id);

    Role selectByName(@Param("name") String name);

    int insert(Role role);

    int update(Role role);

    int deleteById(@Param("id") Long id);
}
