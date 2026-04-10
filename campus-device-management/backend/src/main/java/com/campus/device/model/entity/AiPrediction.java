package com.campus.device.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AiPrediction {
    private Long id;
    private Long deviceId;
    private String predictionType;
    private Double probability;
    private String severity;
    private String description;
    private String recommendation;
    private String modelVersion;
    private LocalDateTime predictedAt;
    private LocalDateTime createdAt;
}
