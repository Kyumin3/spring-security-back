package com.example.lkm.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
public class UserEntity {
    @Id
    private String userId;
    @JsonIgnore
    private String password;
    private String role;
    private String email;
}
