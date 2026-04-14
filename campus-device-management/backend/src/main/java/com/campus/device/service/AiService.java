package com.campus.device.service;

import com.campus.device.model.dto.AiDiagnosisResult;
import com.campus.device.model.dto.AiPredictionResult;

import java.util.List;
import java.util.Map;

public interface AiService {

    AiDiagnosisResult diagnoseFault(Long faultId);

    AiPredictionResult predictDeviceHealth(Long deviceId);

    List<Map<String, Object>> detectAnomalies();

    Map<String, Object> getDeviceHealthScore(Long deviceId);

    List<Map<String, Object>> recommendMaintainer(Long faultId);

    Map<String, Object> analyzeText(String text);
}
