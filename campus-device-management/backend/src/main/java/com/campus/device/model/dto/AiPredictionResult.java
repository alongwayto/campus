package com.campus.device.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPredictionResult {
    private String deviceName;
    private int healthScore;
    private String healthLevel;
    private double failureProbability7Days;
    private double failureProbability30Days;
    private double failureProbability90Days;
    private String riskLevel;
    private List<String> riskFactors;
    private List<String> preventiveActions;
    private String estimatedRemainingLife;
    private String nextMaintenanceRecommendation;
}
