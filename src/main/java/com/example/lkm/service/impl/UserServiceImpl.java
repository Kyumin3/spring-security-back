package com.example.lkm.service.impl;

import com.example.lkm.entity.UserEntity;
import com.example.lkm.repository.UserRepository;
import com.example.lkm.service.UserService;
import com.example.lkm.vo.UserVo;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserEntity> selectAllUser() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Map<String, Object> updataUserRole(String userId, String role) {
        Map<String, Object> result = new HashMap<>();
        int updated = userRepository.updateUserRoleByUsername(userId, role);
        result.put("success", updated > 0);

        boolean isLoggedOut = false;

        if (updated > 0) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            if (userId.equals(currentUserId)) {
                boolean hasMasterRole = auth.getAuthorities().stream()
                        .anyMatch(granted -> "ROLE_MASTER".equals(granted.getAuthority()));
                boolean isRemovingMaster = !role.contains("MASTER");

                if (hasMasterRole && isRemovingMaster) {
                    SecurityContextHolder.clearContext();
                    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                    if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                        HttpSession session = servletRequestAttributes.getRequest().getSession(false);
                        if (session != null) {
                            session.invalidate();
                        }
                    }
                    isLoggedOut = true;
                }
            }
        }

        result.put("isLoggedOut", isLoggedOut);
        return result;
    }

    @Override
    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Override
    public boolean isUserEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserEntity saveUser(UserVo vo) {
        String encodedPassword  = passwordEncoder.encode((vo.getPassword()));
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(vo.getUserId());
        userEntity.setPassword(encodedPassword);
        userEntity.setRole("USER");
        return userRepository.save(userEntity);
    }
}
