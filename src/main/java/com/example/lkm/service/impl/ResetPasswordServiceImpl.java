package com.example.lkm.service.impl;

import com.example.lkm.entity.PasswordResetToken;
import com.example.lkm.entity.UserEntity;
import com.example.lkm.repository.PasswordResetTokenRepository;
import com.example.lkm.repository.UserRepository;
import com.example.lkm.service.ResetPasswordService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final JavaMailSender mailSender;

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.email-secret}")
    private String emailSecretKey;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = emailSecretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public void sendResetEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(15));

        String token = Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();





        if (isRedisAvailable()) {
            redisTemplate.opsForValue().set(token, user.getUserId(), Duration.ofMinutes(15));
        } else {
            PasswordResetToken entity = new PasswordResetToken();
            entity.setToken(token);
            entity.setUserId(user.getUserId());
            entity.setExpiresAt(LocalDateTime.ofInstant(expiry, ZoneId.systemDefault()));
            entity.setUsed(false);
            passwordResetTokenRepository.save(entity);
        }

        MimeMessage message = null;
        try {

            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            String origin = "http://" + ipAddress + ":3000";

            String resetLink = origin + "/reset-password/confirm?token=" + token;

            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject("비밀번호 재설정 링크");
            helper.setText("아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink, false);
            helper.setFrom("samu8912@gmail.com"); // 반드시 설정

            mailSender.send(message);
        } catch (MessagingException | UnknownHostException e) {
            e.printStackTrace();
        }

        mailSender.send(message);
    }

    @Override
    public void updatePassword(String token, String newPassword) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String userId = claims.getSubject();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));


        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);

        if (isRedisAvailable()) {
            redisTemplate.delete(token);
        } else {
            PasswordResetToken entity = passwordResetTokenRepository.findById(token).orElseThrow();
            entity.setUsed(true);
            passwordResetTokenRepository.save(entity);
        }
    }

    @Override
    public void validateResetToken(String token) {

        if (isRedisAvailable()) {
            String userId = redisTemplate.opsForValue().get(token);

            if (userId == null) throw new RuntimeException("토큰 없음 또는 만료됨");
        } else {
            PasswordResetToken entity = passwordResetTokenRepository.findById(token)
                    .orElseThrow(() -> new RuntimeException("토큰 없음"));

            if (entity.isUsed() || entity.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("토큰 만료 또는 이미 사용됨");
            }

        }

        // JWT 서명 검증
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            throw new RuntimeException("토큰이 유효하지 않음");
        }

    }

    //레디스 사용불가시 DB로 토큰관리
    private boolean isRedisAvailable() {
        try {
            redisTemplate.opsForValue().set("healthcheck", "ok", Duration.ofSeconds(5));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
