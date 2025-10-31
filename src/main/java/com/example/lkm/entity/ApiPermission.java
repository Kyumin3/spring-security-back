package com.example.lkm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "API_PERMISSIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ROLES")
    private String roles;

    @Column(name = "METHODS")
    private String methods;

    @Column(name = "PATH")
    private String path;

    @Column(name = "DESCRIPTION")
    private String description;
}
