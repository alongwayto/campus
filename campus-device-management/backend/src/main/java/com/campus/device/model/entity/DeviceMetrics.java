package com.campus.device.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceMetrics {
    private Long id;
    private Long deviceId;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double temperature;
    private Double powerConsumption;
    private Integer errorCount;
    private String networkStatus;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
}
