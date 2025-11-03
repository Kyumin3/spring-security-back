package com.example.lkm.security;

import com.example.lkm.entity.UserEntity;
import com.example.lkm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUserId(username)
                .orElseThrow(() ->{
                    logger.warn("로그인 실패 - 존재하지 않는 사용자: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을수 없습니다.");
                });

        String[] roles = user.getRole().split(",");
        return User.withUsername(user.getUserId())
                .password(user.getPassword())
                .roles(roles)
                .build();
    }
}
