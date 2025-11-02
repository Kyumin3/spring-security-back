package com.example.lkm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LoginController {

    @GetMapping("/api/session")
    public ResponseEntity<?> checkSession(Authentication authentication, HttpServletRequest request) {

        //  토큰을 명시적으로 접근
//        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
//        csrfToken.getToken();

        if (authentication != null && authentication.isAuthenticated()) {

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", authentication.getName());
            // 권한 문자열만 추출해서 리스트로 변환
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            userData.put("roles", roles);
            return ResponseEntity.ok(userData);
        } else {
            Map<String, Object> userData = new HashMap<String, Object>();
            userData.put("username", null);
            List<String> roles = List.of("anonymus");
            userData.put("roles", roles);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userData);
        }
    }

    @GetMapping("/api/csrf")
    public ResponseEntity<?> getCsrfToken() {
        return ResponseEntity.ok().build(); // 아무 내용 없어도 됨
    }


}
