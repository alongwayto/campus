package com.campus.device.dao;

import com.campus.device.model.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogMapper {

    int insert(OperationLog log);

    List<OperationLog> selectAll(@Param("username") String username,
                                  @Param("operation") String operation);
}
