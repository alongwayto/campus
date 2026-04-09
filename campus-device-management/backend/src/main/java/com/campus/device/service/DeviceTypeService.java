package com.campus.device.service;

import com.campus.device.model.entity.DeviceType;

import java.util.List;

public interface DeviceTypeService {

    List<DeviceType> listAll();

    DeviceType getById(Long id);

    void add(DeviceType deviceType);

    void update(DeviceType deviceType);

    void delete(Long id);
}
