package com.example.lkm.service;

import com.example.lkm.entity.UserEntity;
import com.example.lkm.vo.UserVo;

import java.util.List;
import java.util.Map;

public interface UserService {


    public List<UserEntity> selectAllUser();

    public Map<String, Object> updataUserRole(String userId, String role);

    public boolean isUserIdDuplicate(String userId);

    public boolean isUserEmailDuplicate(String email);

    public UserEntity saveUser(UserVo vo);

}
