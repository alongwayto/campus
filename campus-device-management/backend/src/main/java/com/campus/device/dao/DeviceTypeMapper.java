package com.campus.device.dao;

import com.campus.device.model.entity.DeviceType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceTypeMapper {

    List<DeviceType> selectAll();

    DeviceType selectById(@Param("id") Long id);

    int insert(DeviceType deviceType);

    int update(DeviceType deviceType);

    int deleteById(@Param("id") Long id);
}
