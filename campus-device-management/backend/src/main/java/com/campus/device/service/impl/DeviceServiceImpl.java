package com.campus.device.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.campus.device.dao.DeviceMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.dto.DeviceQueryParam;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.Device;
import com.campus.device.service.DeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;

    @Override
    public PageResult<Device> listDevices(DeviceQueryParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<Device> devices = deviceMapper.selectByCondition(param);
        PageInfo<Device> pageInfo = new PageInfo<>(devices);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Device getById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException(404, "Device not found: " + id);
        }
        return device;
    }

    @Override
    @Transactional
    public void addDevice(Device device) {
        device.setCreatedAt(new Date());
        device.setUpdatedAt(new Date());
        if (device.getStatus() == null) {
            device.setStatus(1);
        }
        deviceMapper.insert(device);
    }

    @Override
    @Transactional
    public void updateDevice(Device device) {
        device.setUpdatedAt(new Date());
        int rows = deviceMapper.update(device);
        if (rows == 0) {
            throw new BusinessException(404, "Device not found: " + device.getId());
        }
    }

    @Override
    @Transactional
    public void deleteDevice(Long id) {
        int rows = deviceMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "Device not found: " + id);
        }
    }

    @Override
    public void exportDevices(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("devices", StandardCharsets.UTF_8.name()) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        List<Device> devices = deviceMapper.selectAll();
        EasyExcel.write(response.getOutputStream(), Device.class)
                .sheet("Devices")
                .doWrite(devices);
    }

    @Override
    @Transactional
    public void importDevices(MultipartFile file) throws Exception {
        List<Device> importedDevices = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), Device.class, new AnalysisEventListener<Device>() {
            @Override
            public void invoke(Device data, AnalysisContext context) {
                importedDevices.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel analysis complete, {} rows", importedDevices.size());
            }
        }).sheet().doRead();

        if (!importedDevices.isEmpty()) {
            Date now = new Date();
            for (Device d : importedDevices) {
                d.setCreatedAt(now);
                d.setUpdatedAt(now);
                if (d.getStatus() == null) {
                    d.setStatus(1);
                }
            }
            deviceMapper.batchInsert(importedDevices);
        }
    }
}
