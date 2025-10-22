package com.example.lkm.service;

import com.example.lkm.entity.ApiPermission;
import com.example.lkm.repository.ApiPermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final ApiPermissionRepository permissionRepository;
    private final AntPathMatcher matcher = new AntPathMatcher();

    // 캐시 저장소
    private List<ApiPermission> cachedPermissions = new ArrayList<>();

    @PostConstruct
    public void loadInitialPermissions() {
        cachedPermissions = permissionRepository.findAll();
    }

    public boolean isAuthorized(String path, String method, List<String> userRoles) {
        boolean matchedAnyPath = false;

        for (ApiPermission p : cachedPermissions) {
            if (!matcher.match(p.getPath(), path)) continue; // 경로가 안 맞으면 스킵
            matchedAnyPath = true; // 경로가 하나라도 매칭되면 true

            List<String> allowedRoles = Arrays.stream(p.getRoles().split(","))
                    .map(String::trim)
                    .toList();

            boolean roleMatched = userRoles.stream().anyMatch(allowedRoles::contains);
            if (!roleMatched) continue;


            if (!p.getMethods().contains("ANY") &&
                    !p.getMethods().contains(method)) continue;


            return true; // ✅ 경로 + 역할 + 메서드 모두 만족 → 허용
        }

        return !matchedAnyPath;
    }


    public void reloadPermissions() {
        cachedPermissions = permissionRepository.findAll();
    }

    public List<ApiPermission> getCachedPermissions() {
        return cachedPermissions;
    }
}
