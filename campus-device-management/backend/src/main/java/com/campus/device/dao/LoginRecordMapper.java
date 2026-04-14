package com.campus.device.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginRecordMapper {

    @Insert("INSERT INTO login_record (user_id, username, login_ip, device_info, status, message) " +
            "VALUES (#{userId}, #{username}, #{loginIp}, #{deviceInfo}, #{status}, #{message})")
    int insert(@Param("userId") Long userId, @Param("username") String username,
               @Param("loginIp") String loginIp, @Param("deviceInfo") String deviceInfo,
               @Param("status") int status, @Param("message") String message);

    @Select("SELECT * FROM login_record WHERE user_id = #{userId} ORDER BY login_time DESC LIMIT #{limit}")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
