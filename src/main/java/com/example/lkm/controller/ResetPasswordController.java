package com.example.lkm.controller;

import com.example.lkm.service.ResetPasswordService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Map;

@RestController
@RequestMapping("/api/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @Value("${jwt.email-secret}")
    private String emailSecretKey;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = emailSecretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> body) {
        resetPasswordService.sendResetEmail(body.get("email"));
        return ResponseEntity.ok("이메일 전송 완료");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
//        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return ResponseEntity.ok("유효한 토큰입니다.");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않거나 만료되었습니다.");
        }
//        return ResponseEntity.ok("유효한 토큰입니다.");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body) {
        resetPasswordService.updatePassword(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
}
