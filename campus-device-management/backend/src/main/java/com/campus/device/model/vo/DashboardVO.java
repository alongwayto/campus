package com.campus.device.model.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private Long totalDevices;
    private Long onlineDevices;
    private Long offlineDevices;
    private Long faultDevices;
    private Long pendingFaults;
    private Long resolvedFaultsToday;
    private Long totalUsers;
    private Map<String, Long> deviceStatusDistribution;
    private Map<String, Long> faultTrendLast12Months;
    private List<Map<String, Object>> recentFaults;
    private List<Map<String, Object>> criticalAlerts;
}
