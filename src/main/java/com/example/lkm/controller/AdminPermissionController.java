package com.example.lkm.controller;

import com.example.lkm.entity.ApiPermission;
import com.example.lkm.service.PermissionCacheService;
import com.example.lkm.service.PermissionService;
import com.example.lkm.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final PermissionCacheService permissionCacheService;
    private final PermissionService permissionService;


    @GetMapping("/permissions")
    public ResponseEntity<?> userList() {
        permissionCacheService.reloadPermissions();
        List<ApiPermission> list = permissionCacheService.getCachedPermissions();

        try {

            return ResponseEntity.ok(list);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }


    }

    @PutMapping("/permission/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id, @RequestBody ApiPermission apiPermission) {

        Map<String, Object> result = permissionService.updateApiInfo(id,apiPermission);

        permissionCacheService.reloadPermissions();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/save-permission")
    public ResponseEntity<?> createUser(@RequestBody ApiPermission apiPermission) {
        try {
            ApiPermission savedUser = permissionService.saveApiInfo(apiPermission);

            ApiResponse<ApiPermission> response = new ApiResponse<>(
                    "success",
                    "완료되었습니다.",
                    savedUser
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    "fail",
                    "오류가 발생했습니다.",
                    null
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/permission/{id}")
    public ResponseEntity<?> removePermission(@PathVariable String id) {
        permissionService.removeApiInfo(id);
        return ResponseEntity.ok(null);
    }


    @PostMapping("/reload-permissions")
    public ResponseEntity<?> reloadPermissions() {
        permissionCacheService.reloadPermissions();
        return ResponseEntity.ok("권한 캐시가 갱신되었습니다.");
    }
}
