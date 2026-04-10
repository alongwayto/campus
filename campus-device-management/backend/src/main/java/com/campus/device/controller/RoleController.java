package com.campus.device.controller;

import com.campus.device.aspect.Log;
import com.campus.device.exception.Result;
import com.campus.device.model.entity.Role;
import com.campus.device.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role Management")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "List all roles")
    @GetMapping
    public Result<List<Role>> list() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "Add role")
    @Log(operation = "Add Role")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@RequestBody Role role) {
        roleService.add(role);
        return Result.success();
    }

    @Operation(summary = "Update role")
    @Log(operation = "Update Role")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.update(role);
        return Result.success();
    }

    @Operation(summary = "Delete role")
    @Log(operation = "Delete Role")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }
}
