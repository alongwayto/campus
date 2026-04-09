package com.campus.device.model.dto;

import lombok.Data;

@Data
public class FaultQueryParam {

    private Integer status;
    private Long deviceId;
    private Integer severity;
    private Long assigneeId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
