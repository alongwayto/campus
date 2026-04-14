package com.campus.device.service;

import java.util.List;
import java.util.Map;

public interface AiService {

    /**
     * AI fault diagnosis - analyze fault description and device history
     */
    Map<String, Object> diagnoseFault(Long deviceId, String faultDescription);

    /**
     * Predict fault probability for a device in next 7/30/90 days
     */
    Map<String, Object> predictFault(Long deviceId);

    /**
     * Get device health score (0-100)
     */
    Map<String, Object> getDeviceHealthScore(Long deviceId);

    /**
     * Recommend maintenance personnel for a fault
     */
    List<Map<String, Object>> recommendMaintainer(Long deviceId, String faultDescription);

    /**
     * NLP analysis of fault description
     */
    Map<String, Object> analyzeFaultText(String text);

    /**
     * Get maintenance optimization suggestions for a device
     */
    Map<String, Object> getMaintenanceSuggestion(Long deviceId);

    /**
     * Predict device lifecycle / remaining useful life
     */
    Map<String, Object> predictDeviceLifecycle(Long deviceId);

    /**
     * Detect anomalies across all devices
     */
    List<Map<String, Object>> detectAnomalies();

    /**
     * Recommend spare parts for a fault type
     */
    List<Map<String, Object>> recommendSpareParts(Long deviceId, String faultDescription);
}
