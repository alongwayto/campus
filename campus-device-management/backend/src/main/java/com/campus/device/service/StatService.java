package com.campus.device.service;

import java.util.List;
import java.util.Map;

public interface StatService {

    List<Map<String, Object>> deviceUsageStats();

    List<Map<String, Object>> faultTypeStats();

    List<Map<String, Object>> maintenanceCostMock();

    List<Map<String, Object>> deviceTrend();

    List<Map<String, Object>> faultTrend();
}
