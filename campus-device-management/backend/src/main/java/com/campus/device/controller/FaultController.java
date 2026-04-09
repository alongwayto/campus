package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.model.dto.FaultQueryParam;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.FaultRecord;
import com.campus.device.service.FaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "Fault Management")
@RestController
@RequestMapping("/api/faults")
@RequiredArgsConstructor
public class FaultController {

    private final FaultService faultService;

    @ApiOperation("List faults with pagination and filters")
    @GetMapping
    public Result<PageResult<FaultRecord>> list(FaultQueryParam param) {
        return Result.success(faultService.listFaults(param));
    }

    @ApiOperation("Get fault by ID")
    @GetMapping("/{id}")
    public Result<FaultRecord> getById(@PathVariable Long id) {
        return Result.success(faultService.getById(id));
    }

    @ApiOperation("Report fault")
    @Log(operation = "Report Fault")
    @PostMapping
    public Result<Void> report(@RequestBody FaultRecord fault) {
        faultService.reportFault(fault);
        return Result.success();
    }

    @ApiOperation("Update fault")
    @Log(operation = "Update Fault")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FaultRecord fault) {
        fault.setId(id);
        faultService.updateFault(fault);
        return Result.success();
    }

    @ApiOperation("Delete fault")
    @Log(operation = "Delete Fault")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        faultService.deleteFault(id);
        return Result.success();
    }

    @ApiOperation("Assign fault to maintainer")
    @Log(operation = "Assign Fault")
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public Result<Void> assign(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long assigneeId = body.get("assigneeId");
        faultService.assignFault(id, assigneeId);
        return Result.success();
    }

    @ApiOperation("Resolve fault")
    @Log(operation = "Resolve Fault")
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    public Result<Void> resolve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String notes = body.getOrDefault("notes", "");
        faultService.resolveFault(id, notes);
        return Result.success();
    }
}
