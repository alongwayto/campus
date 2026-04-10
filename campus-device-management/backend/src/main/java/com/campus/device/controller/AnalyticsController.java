package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.model.vo.AnalyticsVO;
import com.campus.device.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "数据分析", description = "故障趋势、设备分布、维护效率等数据分析接口")
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "获取分析数据", description = "获取指定时间范围内的综合分析数据")
    @GetMapping
    public Result<AnalyticsVO> getAnalyticsData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(analyticsService.getAnalyticsData(startDate, endDate));
    }

    @Operation(summary = "故障趋势分析", description = "获取近N个月的故障趋势数据")
    @GetMapping("/fault-trend")
    public Result<Map<String, Object>> getFaultTrend(
            @RequestParam(defaultValue = "12") int months) {
        return Result.success(analyticsService.getFaultTrend(months));
    }

    @Operation(summary = "设备健康报告", description = "获取设备健康状态综合报告")
    @GetMapping("/device-health")
    public Result<Map<String, Object>> getDeviceHealthReport() {
        return Result.success(analyticsService.getDeviceHealthReport());
    }

    @Operation(summary = "维护效率分析", description = "获取维护人员工作效率和满意度统计")
    @GetMapping("/maintenance-efficiency")
    public Result<Map<String, Object>> getMaintenanceEfficiency() {
        return Result.success(analyticsService.getMaintenanceEfficiency());
    }
}
