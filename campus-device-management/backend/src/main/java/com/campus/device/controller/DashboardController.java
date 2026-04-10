package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.model.vo.DashboardVO;
import com.campus.device.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "仪表板", description = "仪表板数据统计接口")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取仪表板数据", description = "获取设备统计、故障趋势等仪表板展示数据")
    @GetMapping
    public Result<DashboardVO> getDashboardData() {
        return Result.success(dashboardService.getDashboardData());
    }
}
