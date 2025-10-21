package com.alpeerkaraca.karga.user.domain;

import com.alpeerkaraca.karga.core.model.BaseClass;
import com.alpeerkaraca.karga.driver.domain.Driver;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLUpdate;

import java.util.UUID;
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE user_id = ?")
@SQLUpdate(sql = "UPDATE users SET updated_at = NOW() WHERE user_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Users extends BaseClass {
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

    @OneToOne(mappedBy = "users", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Driver driver;
}
