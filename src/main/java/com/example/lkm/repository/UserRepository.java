package com.example.lkm.repository;

import com.example.lkm.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUserId(String userId);

    @Modifying
    @Query("UPDATE UserEntity u SET u.role = :role WHERE u.userId = :userId")
    int updateUserRoleByUsername(@Param("userId") String userId, @Param("role") String role);

    boolean existsByUserId(String userId);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
