package com.alpeerkaraca.karga.Models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue
    private UUID userId;
    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    private  String password;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    private double rating;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Timestamp createdAt;

}
