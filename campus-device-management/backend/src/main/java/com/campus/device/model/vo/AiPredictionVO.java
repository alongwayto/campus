package com.campus.device.model.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AiPredictionVO {
    private Long deviceId;
    private String deviceName;
    private String predictionType;
    private Double failureProbability;
    private String riskLevel;
    private String predictedIssue;
    private List<String> symptoms;
    private List<String> recommendations;
    private Map<String, Double> featureImportance;
    private String modelVersion;
    private String predictedAt;
}
