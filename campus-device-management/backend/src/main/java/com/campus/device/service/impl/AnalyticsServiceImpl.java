package com.campus.device.service.impl;

import com.campus.device.model.vo.AnalyticsVO;
import com.campus.device.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    @Override
    public AnalyticsVO getAnalyticsData(String startDate, String endDate) {
        AnalyticsVO vo = new AnalyticsVO();

        Map<String, Long> faultTrend = new LinkedHashMap<>();
        LocalDate start = LocalDate.now().minusMonths(6);
        for (int i = 0; i < 6; i++) {
            String month = start.plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            faultTrend.put(month, (long) (Math.random() * 20 + 5));
        }
        vo.setFaultTrend(faultTrend);

        Map<String, Long> deviceTypeDist = new LinkedHashMap<>();
        deviceTypeDist.put("计算机设备", 120L);
        deviceTypeDist.put("网络设备", 85L);
        deviceTypeDist.put("打印设备", 43L);
        deviceTypeDist.put("安防设备", 67L);
        deviceTypeDist.put("教学设备", 38L);
        vo.setDeviceTypeDistribution(deviceTypeDist);

        Map<String, Double> avgResolution = new LinkedHashMap<>();
        avgResolution.put("网络故障", 2.5);
        avgResolution.put("硬件故障", 8.0);
        avgResolution.put("软件故障", 3.5);
        avgResolution.put("电源故障", 4.0);
        vo.setAvgResolutionTime(avgResolution);

        Map<String, Long> statusDist = new LinkedHashMap<>();
        statusDist.put("待处理", 15L);
        statusDist.put("处理中", 8L);
        statusDist.put("已解决", 45L);
        statusDist.put("已关闭", 32L);
        vo.setFaultStatusDistribution(statusDist);

        vo.setResolutionRate(0.85);
        vo.setAvgResponseTime(3.2);

        return vo;
    }

    @Override
    public Map<String, Object> getFaultTrend(int months) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<Long> faultCounts = new ArrayList<>();
        List<Long> resolvedCounts = new ArrayList<>();

        LocalDate start = LocalDate.now().minusMonths(months - 1);
        for (int i = 0; i < months; i++) {
            LocalDate month = start.plusMonths(i);
            labels.add(month.format(DateTimeFormatter.ofPattern("MM月")));
            long faults = (long) (Math.random() * 20 + 5);
            faultCounts.add(faults);
            resolvedCounts.add((long) (faults * 0.85));
        }

        result.put("labels", labels);
        result.put("faultCounts", faultCounts);
        result.put("resolvedCounts", resolvedCounts);
        return result;
    }

    @Override
    public Map<String, Object> getDeviceHealthReport() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("healthScore", 87.5);
        result.put("onlineRate", 0.92);
        result.put("faultRate", 0.05);
        result.put("maintenanceRate", 0.03);

        List<Map<String, Object>> riskDevices = new ArrayList<>();
        Map<String, Object> device = new LinkedHashMap<>();
        device.put("deviceId", 1L);
        device.put("deviceName", "图书馆查询终端-01");
        device.put("riskScore", 0.78);
        device.put("reason", "多次故障记录");
        riskDevices.add(device);
        result.put("riskDevices", riskDevices);

        return result;
    }

    @Override
    public Map<String, Object> getMaintenanceEfficiency() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("avgResponseTime", 2.3);
        result.put("avgResolutionTime", 5.8);
        result.put("firstTimeFixRate", 0.78);
        result.put("customerSatisfaction", 4.2);

        List<Map<String, Object>> maintainerStats = new ArrayList<>();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", "张维修");
        m.put("tasksCompleted", 28);
        m.put("avgTime", 4.5);
        m.put("score", 4.5);
        maintainerStats.add(m);
        result.put("maintainerStats", maintainerStats);

        return result;
    }
}
