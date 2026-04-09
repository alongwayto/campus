package com.campus.device.service.impl;

import com.campus.device.dao.DeviceTypeMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.entity.DeviceType;
import com.campus.device.service.DeviceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final DeviceTypeMapper deviceTypeMapper;

    @Override
    public List<DeviceType> listAll() {
        return deviceTypeMapper.selectAll();
    }

    @Override
    public DeviceType getById(Long id) {
        DeviceType dt = deviceTypeMapper.selectById(id);
        if (dt == null) {
            throw new BusinessException(404, "DeviceType not found: " + id);
        }
        return dt;
    }

    @Override
    @Transactional
    public void add(DeviceType deviceType) {
        deviceType.setCreatedAt(new Date());
        deviceTypeMapper.insert(deviceType);
    }

    @Override
    @Transactional
    public void update(DeviceType deviceType) {
        int rows = deviceTypeMapper.update(deviceType);
        if (rows == 0) {
            throw new BusinessException(404, "DeviceType not found: " + deviceType.getId());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int rows = deviceTypeMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "DeviceType not found: " + id);
        }
    }
}
