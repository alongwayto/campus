package com.campus.device.service;

import com.campus.device.model.dto.DeviceQueryParam;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.Device;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DeviceService {

    PageResult<Device> listDevices(DeviceQueryParam param);

    Device getById(Long id);

    void addDevice(Device device);

    void updateDevice(Device device);

    void deleteDevice(Long id);

    void exportDevices(HttpServletResponse response) throws Exception;

    void importDevices(MultipartFile file) throws Exception;
}
