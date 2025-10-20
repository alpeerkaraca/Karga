package com.alpeerkaraca.karga.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
    @Query("SELECT u FROM Users u WHERE u.userId = :id AND u.deletedAt IS NOT NULL ")
    Optional<Users> findDeletedById(@Param("id") UUID id);
}
