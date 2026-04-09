package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.model.dto.DeviceQueryParam;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.Device;
import com.campus.device.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Api(tags = "Device Management")
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @ApiOperation("List devices with pagination and filters")
    @GetMapping
    public Result<PageResult<Device>> list(DeviceQueryParam param) {
        return Result.success(deviceService.listDevices(param));
    }

    @ApiOperation("Get device by ID")
    @GetMapping("/{id}")
    public Result<Device> getById(@PathVariable Long id) {
        return Result.success(deviceService.getById(id));
    }

    @ApiOperation("Add device")
    @Log(operation = "Add Device")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public Result<Void> add(@RequestBody Device device) {
        deviceService.addDevice(device);
        return Result.success();
    }

    @ApiOperation("Update device")
    @Log(operation = "Update Device")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Device device) {
        device.setId(id);
        deviceService.updateDevice(device);
        return Result.success();
    }

    @ApiOperation("Delete device")
    @Log(operation = "Delete Device")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success();
    }

    @ApiOperation("Export devices to Excel")
    @Log(operation = "Export Devices")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        deviceService.exportDevices(response);
    }

    @ApiOperation("Import devices from Excel")
    @Log(operation = "Import Devices")
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public Result<Void> importDevices(@RequestParam("file") MultipartFile file) throws Exception {
        deviceService.importDevices(file);
        return Result.success();
    }
}
