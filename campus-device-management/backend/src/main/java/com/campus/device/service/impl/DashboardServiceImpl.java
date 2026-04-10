package com.campus.device.service.impl;

import com.campus.device.model.vo.DashboardVO;
import com.campus.device.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();
        vo.setTotalDevices(353L);
        vo.setOnlineDevices(289L);
        vo.setOfflineDevices(46L);
        vo.setFaultDevices(18L);
        vo.setPendingFaults(7L);
        vo.setResolvedFaultsToday(3L);
        vo.setTotalUsers(45L);

        Map<String, Long> statusDist = new LinkedHashMap<>();
        statusDist.put("在线", 289L);
        statusDist.put("离线", 46L);
        statusDist.put("故障", 18L);
        vo.setDeviceStatusDistribution(statusDist);

        Map<String, Long> trend = new LinkedHashMap<>();
        trend.put("1月", 12L); trend.put("2月", 8L); trend.put("3月", 15L);
        trend.put("4月", 10L); trend.put("5月", 18L); trend.put("6月", 22L);
        trend.put("7月", 16L); trend.put("8月", 14L); trend.put("9月", 20L);
        trend.put("10月", 17L); trend.put("11月", 13L); trend.put("12月", 11L);
        vo.setFaultTrendLast12Months(trend);

        return vo;
    }
}
