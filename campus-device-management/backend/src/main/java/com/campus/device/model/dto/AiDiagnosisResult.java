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
public class AiDiagnosisResult {
    private String deviceName;
    private String faultTitle;
    private List<DiagnosisItem> diagnoses;
    private List<String> recommendedActions;
    private String urgencyLevel;
    private double overallConfidence;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisItem {
        private String possibleCause;
        private double confidence;
        private String category;
        private List<String> troubleshootingSteps;
    }
}
