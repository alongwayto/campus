package com.campus.device.service;

import com.campus.device.model.vo.AnalyticsVO;
import java.util.Map;

public interface AnalyticsService {
    AnalyticsVO getAnalyticsData(String startDate, String endDate);
    Map<String, Object> getFaultTrend(int months);
    Map<String, Object> getDeviceHealthReport();
    Map<String, Object> getMaintenanceEfficiency();
}
