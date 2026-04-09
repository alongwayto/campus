package com.campus.device.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private Long id;

    @ExcelProperty("设备名称")
    private String name;

    private Long deviceTypeId;

    @ExcelProperty("位置")
    private String location;

    /**
     * 0=offline, 1=online, 2=fault
     */
    @ExcelProperty("状态")
    private Integer status;

    @ExcelProperty("序列号")
    private String serialNumber;

    @ExcelProperty("制造商")
    private String manufacturer;

    @ExcelProperty("购买日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date purchaseDate;

    @ExcelProperty("保修到期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date warrantyExpiry;

    @ExcelProperty("描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updatedAt;
}
