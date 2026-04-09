package com.campus.device.model.dto;

import lombok.Data;

@Data
public class DeviceQueryParam {

    private String name;
    private Integer status;
    private Long deviceTypeId;
    private String location;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
