package com.campus.device.service.impl;

import com.campus.device.dao.LogMapper;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.OperationLog;
import com.campus.device.service.LogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogMapper logMapper;

    @Override
    public PageResult<OperationLog> listLogs(String username, String operation, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> logs = logMapper.selectAll(username, operation);
        PageInfo<OperationLog> pageInfo = new PageInfo<>(logs);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}
