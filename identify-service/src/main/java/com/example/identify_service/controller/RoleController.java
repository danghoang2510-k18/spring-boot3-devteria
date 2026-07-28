package com.example.identify_service.controller;


import com.example.identify_service.dto.request.ApiResponse;
import com.example.identify_service.dto.request.PermissionRequest;
import com.example.identify_service.dto.request.RoleRequest;
import com.example.identify_service.dto.response.PermissionResponse;
import com.example.identify_service.dto.response.RoleResponse;
import com.example.identify_service.service.PermissionService;
import com.example.identify_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@Slf4j
public class RoleController {
   RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request)
    {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll()
    {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{roleName}")
    ApiResponse delete(@PathVariable String roleName)
    {
        roleService.delete(roleName);
        return ApiResponse.builder()
                .message("Deleted")
                .build();
    }

}
