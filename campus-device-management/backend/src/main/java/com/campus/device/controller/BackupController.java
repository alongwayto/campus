package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.service.BackupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Database Backup")
@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @ApiOperation("Create database backup")
    @Log(operation = "Database Backup")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> createBackup() throws Exception {
        String fileName = backupService.backupDatabase();
        return Result.success(fileName);
    }

    @ApiOperation("List available backups")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<String>> listBackups() {
        return Result.success(backupService.listBackups());
    }
}
