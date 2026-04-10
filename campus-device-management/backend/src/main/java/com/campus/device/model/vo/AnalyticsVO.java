package com.campus.device.model.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AnalyticsVO {
    private Map<String, Long> faultTrend;
    private Map<String, Long> deviceTypeDistribution;
    private Map<String, Double> avgResolutionTime;
    private List<Map<String, Object>> topFaultDevices;
    private Map<String, Long> faultStatusDistribution;
    private Map<String, Long> monthlySummary;
    private Double resolutionRate;
    private Double avgResponseTime;
}
