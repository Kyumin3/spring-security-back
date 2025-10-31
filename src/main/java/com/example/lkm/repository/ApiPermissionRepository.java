package com.example.lkm.repository;

import com.example.lkm.entity.ApiPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiPermissionRepository extends JpaRepository<ApiPermission, Long> {
    List<ApiPermission> findAll();

    @Modifying
    @Query("UPDATE ApiPermission u SET u.roles = :roles, u.methods = :methods, u.path = :path, u.description =:description WHERE u.id = :id")
    int updateApiInfoById(@Param("id") String id, @Param("roles") String roles, @Param("methods") String methods, @Param("path") String path, @Param("description") String description);
}
