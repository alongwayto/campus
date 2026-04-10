package com.campus.device.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AiModelData {
    private Long id;
    private Long deviceId;
    private Long faultId;
    private String featureData;
    private String label;
    private String dataType;
    private LocalDateTime createdAt;
}
