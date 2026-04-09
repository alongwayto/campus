package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.model.entity.DeviceType;
import com.campus.device.service.DeviceTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Device Type Management")
@RestController
@RequestMapping("/api/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @ApiOperation("List all device types")
    @GetMapping
    public Result<List<DeviceType>> list() {
        return Result.success(deviceTypeService.listAll());
    }

    @ApiOperation("Get device type by ID")
    @GetMapping("/{id}")
    public Result<DeviceType> getById(@PathVariable Long id) {
        return Result.success(deviceTypeService.getById(id));
    }

    @ApiOperation("Add device type")
    @Log(operation = "Add DeviceType")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@RequestBody DeviceType deviceType) {
        deviceTypeService.add(deviceType);
        return Result.success();
    }

    @ApiOperation("Update device type")
    @Log(operation = "Update DeviceType")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody DeviceType deviceType) {
        deviceType.setId(id);
        deviceTypeService.update(deviceType);
        return Result.success();
    }

    @ApiOperation("Delete device type")
    @Log(operation = "Delete DeviceType")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        deviceTypeService.delete(id);
        return Result.success();
    }
}
