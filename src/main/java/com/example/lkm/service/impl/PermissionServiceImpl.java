package com.example.lkm.service.impl;

import com.example.lkm.entity.ApiPermission;
import com.example.lkm.repository.ApiPermissionRepository;
import com.example.lkm.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private final ApiPermissionRepository apiPermissionRepository;
    @Override
    public List<ApiPermission> selectAllApi() {
        return apiPermissionRepository.findAll();
    }

    @Override
    public Map<String, Object> updateApiInfo(String id, ApiPermission apiPermission) {

        apiPermissionRepository.updateApiInfoById(id,apiPermission.getRoles(),apiPermission.getMethods(),apiPermission.getPath());

        return Map.of();
    }

    @Override
    public ApiPermission saveApiInfo(ApiPermission apiPermission) {
        return apiPermissionRepository.save(apiPermission);
    }

    @Override
    public void removeApiInfo(String id) {
        ApiPermission entity = new ApiPermission();
        entity.setId(Long.valueOf(id));
        apiPermissionRepository.delete(entity);
    }
}
