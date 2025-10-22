package com.example.lkm.service;

import com.example.lkm.entity.ApiPermission;

import java.util.List;
import java.util.Map;

public interface PermissionService {

    public List<ApiPermission> selectAllApi();

    public Map<String, Object> updateApiInfo(String id, ApiPermission apiPermission);

    public ApiPermission saveApiInfo(ApiPermission apiPermission);

    public void removeApiInfo(String id);
}
