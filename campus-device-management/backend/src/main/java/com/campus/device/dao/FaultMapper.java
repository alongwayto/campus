package com.campus.device.dao;

import com.campus.device.model.dto.FaultQueryParam;
import com.campus.device.model.entity.FaultRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FaultMapper {

    List<FaultRecord> selectByCondition(FaultQueryParam param);

    FaultRecord selectById(@Param("id") Long id);

    int insert(FaultRecord fault);

    int update(FaultRecord fault);

    int deleteById(@Param("id") Long id);

    List<Map<String, Object>> countBySeverity();

    List<Map<String, Object>> countByMonth(@Param("months") int months);
}
