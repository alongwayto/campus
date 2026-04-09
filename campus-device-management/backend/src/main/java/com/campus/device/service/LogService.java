package com.campus.device.service;

import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.OperationLog;

public interface LogService {

    PageResult<OperationLog> listLogs(String username, String operation, int pageNum, int pageSize);
}
