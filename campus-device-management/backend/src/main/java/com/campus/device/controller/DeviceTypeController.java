package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.model.entity.DeviceType;
import com.campus.device.service.DeviceTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Device Type Management")
@RestController
@RequestMapping("/api/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @Operation(summary = "List all device types")
    @GetMapping
    public Result<List<DeviceType>> list() {
        return Result.success(deviceTypeService.listAll());
    }

    @Operation(summary = "Get device type by ID")
    @GetMapping("/{id}")
    public Result<DeviceType> getById(@PathVariable Long id) {
        return Result.success(deviceTypeService.getById(id));
    }

    @Operation(summary = "Add device type")
    @Log(operation = "Add DeviceType")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@RequestBody DeviceType deviceType) {
        deviceTypeService.add(deviceType);
        return Result.success();
    }

    @Operation(summary = "Update device type")
    @Log(operation = "Update DeviceType")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody DeviceType deviceType) {
        deviceType.setId(id);
        deviceTypeService.update(deviceType);
        return Result.success();
    }

    @Operation(summary = "Delete device type")
    @Log(operation = "Delete DeviceType")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        deviceTypeService.delete(id);
        return Result.success();
    }
}
