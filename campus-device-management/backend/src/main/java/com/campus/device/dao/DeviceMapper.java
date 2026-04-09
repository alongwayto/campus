package com.campus.device.dao;

import com.campus.device.model.dto.DeviceQueryParam;
import com.campus.device.model.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceMapper {

    List<Device> selectByCondition(DeviceQueryParam param);

    Device selectById(@Param("id") Long id);

    List<Device> selectAll();

    int insert(Device device);

    int batchInsert(@Param("devices") List<Device> devices);

    int update(Device device);

    int deleteById(@Param("id") Long id);

    List<Map<String, Object>> countByStatus();

    List<Map<String, Object>> countByMonthRegistered(@Param("months") int months);
}
