package com.campus.device.dao;

import com.campus.device.model.entity.LoginRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginRecordMapper {

    List<LoginRecord> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    int insert(LoginRecord record);
}
