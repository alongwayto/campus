package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.service.StatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "Statistics")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @ApiOperation("Device status statistics")
    @GetMapping("/device-status")
    public Result<List<Map<String, Object>>> deviceStatus() {
        return Result.success(statService.deviceUsageStats());
    }

    @ApiOperation("Fault type / severity statistics")
    @GetMapping("/fault-types")
    public Result<List<Map<String, Object>>> faultTypes() {
        return Result.success(statService.faultTypeStats());
    }

    @ApiOperation("Device registration trend (last 12 months)")
    @GetMapping("/device-trend")
    public Result<List<Map<String, Object>>> deviceTrend() {
        return Result.success(statService.deviceTrend());
    }

    @ApiOperation("Fault occurrence trend (last 6 months)")
    @GetMapping("/fault-trend")
    public Result<List<Map<String, Object>>> faultTrend() {
        return Result.success(statService.faultTrend());
    }

    @ApiOperation("Monthly maintenance cost (mock data)")
    @GetMapping("/maintenance-cost")
    public Result<List<Map<String, Object>>> maintenanceCost() {
        return Result.success(statService.maintenanceCostMock());
    }
}
