package com.campus.device.dao;

import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserProfileMapper {

    @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
    Map<String, Object> selectByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO user_profile (user_id, avatar_url, department, position, last_login_time, last_login_ip, last_login_device) " +
            "VALUES (#{userId}, #{avatarUrl}, #{department}, #{position}, #{lastLoginTime}, #{lastLoginIp}, #{lastLoginDevice})")
    int insert(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl,
               @Param("department") String department, @Param("position") String position,
               @Param("lastLoginTime") Date lastLoginTime, @Param("lastLoginIp") String lastLoginIp,
               @Param("lastLoginDevice") String lastLoginDevice);

    @Update("UPDATE user_profile SET department = #{department}, position = #{position}, updated_at = NOW() WHERE user_id = #{userId}")
    int updateProfile(@Param("userId") Long userId, @Param("department") String department, @Param("position") String position);

    @Update("UPDATE user_profile SET avatar_url = #{avatarUrl}, updated_at = NOW() WHERE user_id = #{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);

    @Update("UPDATE user_profile SET last_login_time = #{loginTime}, last_login_ip = #{loginIp}, last_login_device = #{loginDevice}, updated_at = NOW() WHERE user_id = #{userId}")
    int updateLoginInfo(@Param("userId") Long userId, @Param("loginTime") Date loginTime,
                        @Param("loginIp") String loginIp, @Param("loginDevice") String loginDevice);
}
