package com.campus.device.dao;

import com.campus.device.model.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    UserProfile selectByUserId(@Param("userId") Long userId);

    int insert(UserProfile profile);

    int update(UserProfile profile);
}
