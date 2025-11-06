package com.example.lkm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PWRESET_TOKEN")
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @Column(name = "TOKEN")
    private String token;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;

    @Column(name = "USED")
    private boolean used;
}
