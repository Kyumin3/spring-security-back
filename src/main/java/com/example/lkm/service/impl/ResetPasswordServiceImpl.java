package com.example.lkm.service.impl;

import com.example.lkm.entity.UserEntity;
import com.example.lkm.repository.UserRepository;
import com.example.lkm.service.ResetPasswordService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final JavaMailSender mailSender;

    private final UserRepository userRepository;

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

        String token = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String resetLink = "http://localhost:3000/reset-password/confirm?token=" + token;

//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("비밀번호 재설정 링크");
//        message.setText("아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink);

        MimeMessage message = null;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject("비밀번호 재설정 링크");
            helper.setText("아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink, false);
            helper.setFrom("samu8912@gmail.com"); // 반드시 설정

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // 또는 로깅 처리
            // 사용자에게 오류 메시지 반환하거나 실패 처리
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
    }
}
