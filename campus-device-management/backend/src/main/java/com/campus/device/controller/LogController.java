package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.OperationLog;
import com.campus.device.service.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Operation Logs")
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "List operation logs with pagination")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<OperationLog>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(logService.listLogs(username, operation, pageNum, pageSize));
    }
}
