package com.ezadimn.modules.system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sys_user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
}
